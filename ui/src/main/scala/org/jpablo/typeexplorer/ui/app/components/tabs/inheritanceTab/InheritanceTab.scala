package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.widgets.Icons
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

object InheritanceTab:

  val gridColumn = styleProp("grid-column")

  def apply(
      appState: AppState,
      inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]
  ): ReactiveHtmlElement[HTMLDivElement] =
    val canvasContainer =
      CanvasContainer(inheritanceSvgDiagram, appState.inheritanceTab)

    val showPackagesTree = Var(false)

    // --- grid container: 4 columns, 2 rows ---
    div(
      cls := "grid h-full grid-cols-[46px_1fr_4fr_0.75fr] grid-rows-[3em_auto]",
      LeftSideMenu(showPackagesTree),
      PackagesTreeComponent(appState).amend(
        cls.toggle("hidden") <-- !showPackagesTree.signal
      ),
      Toolbar(
        appState,
        inheritanceSvgDiagram,
        canvasContainer.ref.getBoundingClientRect()
      ).amend(gridColumn <-- showPackagesTree.signal.switch("3 / 5", "2 / 5")),
      canvasContainer.amend(
        gridColumn <-- showPackagesTree.signal.switch("3 / 4", "2 / 4")
      ),
      SelectionSidebar(appState, inheritanceSvgDiagram)
    )

  private def LeftSideMenu(active: Var[Boolean]) =
    div(
      cls := "row-start-1 row-end-3 flex justify-center border-r border-slate-300",
      ul(
        cls := "menu menu-sm rounded-box",
        li(
          Icons.folder.amend(
            cls.toggle("active") <-- active.signal,
            onClick --> active.toggle()
          )
        )
      )
    )

end InheritanceTab
