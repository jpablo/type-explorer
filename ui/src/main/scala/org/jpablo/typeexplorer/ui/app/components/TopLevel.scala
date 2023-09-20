package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.Project
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram

def TopLevel(
    project: Project,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    documents: EventStream[List[TextDocumentsWithSource]]
) =
  div(
    cls := "drawer drawer-end",
    input(idAttr := "drawer-1", tpe := "checkbox", cls := "drawer-toggle"),
    div(
      cls := "drawer-content flex flex-col h-screen",
      AppHeader(project.basePaths),
      TabsArea(project, inheritanceSvgDiagram, documents),
      AppFooter,
      project.config.signal
        .map(_.advancedMode)
        .childWhenTrue:
          div(
            div(
              child.text <-- project.inheritanceTabState.canvasSelectionR.signal
                .map(ds => s"canvasSelection: ${ds.size}")
            ),
            div(
              child.text <-- project.inheritanceTabState.activeSymbolsR.signal
                .map(ss => s"activeSymbols: ${ss.size}")
            )
          )
    ),
    AppConfigDrawer(project.config)
  )
