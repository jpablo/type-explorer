package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTab
import org.jpablo.typeexplorer.ui.domUtils.*
import org.scalajs.dom.{HTMLDivElement, HTMLElement}

def TabsArea(
    appState: AppState,
    documents: EventStream[List[TextDocumentsWithSource]]
): Div =
  val tabs: Signal[Vector[ReactiveHtmlElement[HTMLElement]]] =
    appState.tabStates
      .split(_.pageId)(renderTab(appState))
      .map(_.flatten)

  // -----------------------------
  div(
    role := "tablist",
    cls := "tabs tabs-lifted h-full w-full te-tabs-area",
    children <-- tabs
  )

def renderTab(appState: AppState)(
    pageId: String,
    initialTabState: InheritanceTabState,
    tabStateR: Signal[InheritanceTabState]
): Seq[ReactiveHtmlElement[HTMLElement]] =
  val activePageId: Signal[String] = appState.activeProject.getActivePageId
  Seq(
    input(
      role := "tab",
      tpe := "radio",
      checked <-- activePageId.map(pageId == _),
      cls := "tab",
      name := "tabs_area",
      ariaLabel := s"Inheritance",
      onClick --> appState.setActivePage(pageId)
    ),
    div(
      role := "tabpanel",
      cls := "tab-content bg-base-100 border-base-300 rounded-box h-full",
      child <-- tabStateR.map(InheritanceTab(appState, _))
    )
  )
