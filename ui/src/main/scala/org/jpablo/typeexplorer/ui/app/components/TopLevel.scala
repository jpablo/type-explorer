package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.toggle
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.{SelectedSymbols, AppState}
import com.raquo.airstream.core.EventStream
import org.scalajs.dom
import org.jpablo.typeexplorer.shared.models.Symbol
import com.raquo.airstream.state.Var
import com.raquo.airstream.core.Observer

def TopLevel: Div =
  val $newDiagramType = EventBus[DiagramType]
  val $selectedUri    = EventBus[Path]
  val appState        = AppState()
  val $documents      = fetchDocuments(appState.$projectPath)
  val $classes        = fetchClasses(appState.$projectPath)
  val $inheritance    = appState.$inheritanceSelection.flatMap(fetchInheritanceSVGDiagram)

  val $setSymbol = EventBus[Symbol]()
  
  val updateSymbolSelection = 
    appState.$diagramSelection.updater[Symbol](_ `toggle` _)
  
  
  div(
    idAttr := "te-toplevel",
    $setSymbol --> updateSymbolSelection,
    AppHeader($newDiagramType, appState.projectPath),
    TabsArea(
      appState.$projectPath,
      $documents,
      $inheritance,
      $classes,
      appState.selectedSymbols,
      $setSymbol
    ),
    AppFooter,
    div(child.text <-- appState.$diagramSelection.signal.map(_.toString)),
  )
