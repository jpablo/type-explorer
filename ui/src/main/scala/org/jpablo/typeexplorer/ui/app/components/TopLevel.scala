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
  appState             : AppState,
  inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
  documents: EventStream[List[TextDocumentsWithSource]]
) =
  div(
    cls := "drawer drawer-end",
    input(idAttr := "drawer-1", tpe := "checkbox", cls := "drawer-toggle"),
    div(cls := "drawer-content flex flex-col h-full",
      AppHeader(appState.basePaths),
      TabsArea(appState, inheritanceSvgDiagram, documents),
      AppFooter,
      appState.$appConfig.signal.map(_.advancedMode).childWhenTrue:
        div(
          div(child.text <-- appState.inheritanceTabState.canvasSelectionR.signal.map(ds => s"canvasSelection: ${ds.size}")),
          div(child.text <-- appState.inheritanceTabState.activeSymbolsR.signal.map(ss => s"activeSymbols: ${ss.size}")),
        )
    ),
    AppConfigDrawer(appState.$appConfig),
  )
