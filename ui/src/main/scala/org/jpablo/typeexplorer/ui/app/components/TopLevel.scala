package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.{SelectedSymbols, AppState}
import com.raquo.airstream.core.EventStream
import org.scalajs.dom

def TopLevel: Div =
  val $newDiagramType = EventBus[DiagramType]
  val $selectedUri    = EventBus[Path]
  val appState        = AppState()
  val $documents      = fetchDocuments(appState.$projectPath)
  val $classes        = fetchClasses(appState.$projectPath)
  val $inheritance    = appState.$inheritanceSelection.flatMap(fetchInheritanceSVGDiagram)

  div(
    idAttr := "te-toplevel",
    AppHeader($newDiagramType, appState.projectPath),
    TabsArea(
      appState.$projectPath,
      $documents,
      $inheritance,
      $classes,
      appState.selectedSymbols
    ),
    AppFooter
  )
