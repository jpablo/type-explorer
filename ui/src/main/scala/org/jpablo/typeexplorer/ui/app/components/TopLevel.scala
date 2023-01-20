package org.jpablo.typeexplorer.ui.app.components

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.jpablo.typeexplorer.ui.app.toggle
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource

def TopLevel(
  appState: AppState,
  $inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
  $documents: EventStream[List[TextDocumentsWithSource]]
) =
  div(
    cls := "flex flex-col h-full",
    AppHeader(appState.projectPath),
    TabsArea(appState, $inheritanceSvgDiagram, $documents),
    AppFooter,
    appState.$devMode.signal.childWhenTrue {
      div(
        div(child.text <-- appState.inheritanceTabState.$canvasSelection.signal.map(ds => s"canvasSelection: ${ds.size}")),
        div(child.text <-- appState.inheritanceTabState.$activeSymbols.signal.map(ss => s"activeSymbols: ${ss.size}")),
      )
    }
  )
