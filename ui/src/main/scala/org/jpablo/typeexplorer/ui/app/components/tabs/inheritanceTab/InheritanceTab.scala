package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.widgets.Icons
import org.jpablo.typeexplorer.ui.widgets.Icons.folderIcon
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

object InheritanceTab:

  def apply(appState: AppState)(
      tabState: InheritanceTabState,
      inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]
  ): ReactiveHtmlElement[HTMLDivElement] =
    val canvasContainer =
      CanvasContainer(tabState, inheritanceSvgDiagram)

    val showPackagesTree = Var(false)

    // --- grid container: 4 columns, 2 rows ---
    div(
      cls := "h-full w-full relative",
      canvasContainer,
      Toolbar(
        appState,
        tabState,
        inheritanceSvgDiagram,
        canvasContainer.ref.getBoundingClientRect()
      ),
      LeftSideMenu(showPackagesTree),
      PackagesTreeComponent(appState, tabState).amend(
        cls.toggle("hidden") <-- !showPackagesTree.signal
      ),
      SelectionSidebar(appState, tabState, inheritanceSvgDiagram)
    )

  private def LeftSideMenu(active: Var[Boolean]) =
    div(
      cls := "bg-base-100 rounded-box justify-center absolute left-0 top-2/4 -translate-y-2/4 z-10",
      ul(
        cls := "menu menu-sm rounded-box",
        li(
          a.folderIcon.amend(
            cls.toggle("active") <-- active.signal,
            onClick --> active.toggle()
          )
        )
      )
    )

end InheritanceTab
