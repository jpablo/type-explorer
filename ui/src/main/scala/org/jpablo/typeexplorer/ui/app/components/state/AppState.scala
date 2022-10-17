package org.jpablo.typeexplorer.ui.app.components.state

import org.scalajs.dom
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.*
import org.jpablo.typeexplorer.ui.app.client.fetchInheritanceSVGDiagram
import org.jpablo.typeexplorer.shared.models.Symbol
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchInheritanceSVGDiagram}
import io.laminext.core.StoredString
import com.raquo.airstream.core.Signal
import app.tulz.tuplez.Composition.Aux


case class AppState(
  selectedSymbols: SelectedSymbols = SelectedSymbols(),
  projectPath: StoredString = storedString("projectPath", initial = "")
):
  val $projectPath: Signal[Path] = 
    projectPath.signal.map(Path.apply)

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: EventStream[(Path, Set[(Symbol, Set[Related])], Options)] =
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
      


case class SelectedSymbols(
  symbols: Var[Map[Symbol, Selection]] = Var(Map.empty),
  options: Var[Options] = Var(Options())
)

case class Selection(
  current : Boolean = false,
  parents : Boolean = false,
  children: Boolean = false,
):
  def allEmpty = !current && !parents && !children

object Selection:
  def empty = Selection()


