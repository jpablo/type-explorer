package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import com.raquo.laminar.nodes.ReactiveHtmlElement


def NavTabs(mods: Modifier[ReactiveHtmlElement.Base]*) =
  div(cls := "tabs", mods)

def TabContent(mods: Modifier[ReactiveHtmlElement.Base]*) =
  div(cls := "tab-content", mods)


class Tab(activeTab: Var[Int], title: String, index: Int):
  def NavItem =
    a(
      cls := "tab tab-lifted",
      cls.toggle("tab-active", "") <-- activeTab.signal.map(_ == index),
      onClick.mapTo(index) --> activeTab,
      title
    )

  def Pane(target: String, content: Element) =
    div(
      idAttr := target,
      cls.toggle("hidden", "") <-- activeTab.signal.map(_ != index),
      tabIndex := index,
      content
    )


def Tabs(headers: String*): List[Tab] =
  val activeTab = Var(0)
  headers.zipWithIndex.map((h, i) => Tab(activeTab, h, i)).toList

