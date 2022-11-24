package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.scalajs.dom
import zio.prelude.fx.ZPure
import zio.Tag

import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.ui.app.Path
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.core.Signal


case class AppState(
  selectedSymbols: SelectedSymbols = SelectedSymbols(),
  projectPath: StoredString = storedString("projectPath", initial = ""),
  $diagramSelection: Var[Set[models.Symbol]] = Var(Set.empty)
):
  val $projectPath: Signal[Path] = 
    projectPath.signal.map(Path.apply)

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: EventStream[(Path, Set[(models.Symbol, Set[Related])], Options)] =
    val $requestBody =
      selectedSymbols.symbols.signal.changes.map { symbols =>
        symbols.transform { (symbol, selection) => selection match
          case Selection(true, false, false) => Set.empty
          case Selection(_   , true , false) => Set(Related.Parents)
          case Selection(_   , false, true ) => Set(Related.Children)
          case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
          case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
        }.toSet
      }
    $projectPath
      .combineWith($requestBody.toSignal(Set.empty), selectedSymbols.options.signal)
      .changes


end AppState




case class SelectedSymbols(
  symbols: Var[Map[models.Symbol, Selection]] = Var(Map.empty),
  ignored: Var[Set[models.Symbol]] = Var(Set.empty),
  options: Var[Options] = Var(Options())
):

  def enableParents(diagram: InheritanceDiagram)(symbol: models.Symbol): Unit =
    val parents: Set[models.Symbol] = diagram.allParents(symbol).namespaces.map(_.symbol)
    println(parents)
    symbols.update { (symbols: Map[models.Symbol, Selection]) =>
      parents.foldLeft(symbols) { (acc, sym) => 
        val selection0 = symbols.getOrElse(symbol, Selection.empty)
        val selection1 = if !selection0.current then selection0.copy(current = true) else selection0
        symbols.updated(sym, selection1)
      }
    }

  def symbolsUpdater(ns: models.Namespace, modifyField: PathLazyModify[Selection, Boolean]) =
    symbols.updater[Boolean] { (symbols, b) =>
      val selection0 = symbols.getOrElse(ns.symbol, Selection.empty)
      val selection1 = modifyField.setTo(b)(selection0)
      if selection1.allEmpty then
        symbols - ns.symbol
      else
        symbols + (ns.symbol -> selection1)
    }      

  def selection(symbol: models.Symbol): Signal[Selection] = 
    symbols.signal.map(_.getOrElse(symbol, Selection.empty))    

end SelectedSymbols

case class Selection(
  current : Boolean = false, // <--
  parents : Boolean = false,
  children: Boolean = false,
):
  def allEmpty = !current && !parents && !children

object Selection:
  def empty = Selection()





type Service[A] = 
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  val $diagram           = service[EventStream[InheritanceDiagram]]
  val $documents         = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath       = service[Signal[Path]]

  val $selectedNamespace = service[EventBus[Symbol]]
  val $diagramSelection  = service[Var[Set[Symbol]]]

  val $svgDiagram        = service[EventStream[dom.SVGElement]]
  val projectPath        = service[StoredString]
  val selectedSymbols    = service[SelectedSymbols]

