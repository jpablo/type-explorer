package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram

def TopLevel(
    appState: AppState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    documents: EventStream[List[TextDocumentsWithSource]],
    selectedProject: EventBus[ProjectId],
    deleteProject: EventBus[ProjectId]
) =
  div(
    cls := "drawer drawer-end",
    input(idAttr := "drawer-1", tpe := "checkbox", cls := "drawer-toggle"),
    div(
      cls := "drawer-content flex flex-col h-screen",
      AppHeader(appState, selectedProject, deleteProject),
      TabsArea(appState, inheritanceSvgDiagram, documents),
      AppFooter,
      // -------- advanced mode --------
      appState.advancedMode.childWhenTrue:
        div(
          div(
            child.text <-- appState.inheritanceTab.canvasSelection.signal
              .map(ds => s"canvasSelection: ${ds.size}")
          ),
          div(
            child.text <-- appState.inheritanceTab.activeSymbols.signal
              .map(ss => s"activeSymbols: ${ss.size}")
          )
        )
    ),
    AppConfigDrawer(appState.activeProject.project)
  )
