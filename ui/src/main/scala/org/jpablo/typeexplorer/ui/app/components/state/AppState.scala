package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import zio.Tag
import zio.json.*


case class AppState(
  inheritanceTabState: InheritanceTabState,
  projectPath        : StoredString,
  $devMode           : Var[Boolean],
):
  val $projectPath = projectPath.signal.map(Path.apply)


object AppState:
  def build(fetchDiagram: Path => Signal[InheritanceDiagram]) =
    given owner: Owner = OneTimeOwner(() => ())
    val projectPath = storedString("projectPath", initial = "")
    val activeSymbolsJson = storedString("activeSymbols", initial = "{}")
    val $projectPath = projectPath.signal.map(Path.apply)
    AppState(
      inheritanceTabState = InheritanceTabState(
        activeSymbolsJson,
        $projectPath,
        $projectPath.flatMap(fetchDiagram),
      ),
      projectPath = projectPath,
      $devMode = Var(true)
    )



