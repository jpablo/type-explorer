package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.widgets.Icons

object InheritanceTab:

  val gridColumn = styleProp("grid-column")

  def apply(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]) =
    val canvasContainer = CanvasContainer(inheritanceSvgDiagram, appState.inheritanceTabState)

    val showPackagesTree = Var(false)
    val middleColumn = showPackagesTree.signal.switch("3 / 4", "2 / 4")

    // --- grid container: 4 columns, 2 rows ---
    div(cls := "grid h-full grid-cols-[46px_1fr_4fr_0.75fr] grid-rows-[3em_auto]",
      LeftSideMenu(showPackagesTree),
      PackagesTreeComponent(appState).amend(cls.toggle("hidden") <-- !showPackagesTree.signal),
      Toolbar(appState, inheritanceSvgDiagram, canvasContainer.ref.getBoundingClientRect()).amend(gridColumn <-- middleColumn),
      canvasContainer.amend(gridColumn <-- middleColumn),
      SelectionSidebar(appState, inheritanceSvgDiagram)
    )

  private def LeftSideMenu(active: Var[Boolean]) =
    div(cls := "row-start-1 row-end-3 flex justify-center bg-slate-100 border-r border-slate-300",
      ul(cls := "menu menu-compact",
        li(cls.toggle("bg-primary") <-- active.signal,
          Icons.folder.amend(onClick --> active.toggle()),
        )
      )
    )

end InheritanceTab

