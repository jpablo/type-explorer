package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement


def NavTabs(mods: Modifier[ReactiveHtmlElement.Base]*) =
  ul(cls := "nav nav-tabs", role := "tablist", mods)

def TabContent(mods: Modifier[ReactiveHtmlElement.Base]*) =
  div(cls := "tab-content", mods)


class Tab(target: String, active: Boolean = false):
  def NavItem(title: String) =
    li(
      cls := "nav-item",
      role := "presentation",
      button(
        cls := "nav-link",
        cls := (if active then "active" else ""),
        dataAttr("bs-toggle") := "tab",
        dataAttr("bs-target") := "#" + target,
        tpe := "button",
        role := "tab",
        title
      )
    )
  
  def Pane(index: Int, content: Element) =
    div(
      idAttr := target,
      cls := "tab-pane fade",
      cls := (if active then "show active" else ""),
      role := "tabpanel",
      tabIndex := index,
      content
    )



