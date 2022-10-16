package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

class Tab(active: Boolean, target: String):
  def header(title: String) =
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
  
  def body(index: Int, content: ReactiveHtmlElement[dom.html.Element]) =
    div(
      idAttr := target,
      cls := "tab-pane fade",
      cls := (if active then "show active" else ""),
      role := "tabpanel",
      tabIndex := index,
      content
    )



