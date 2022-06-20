package org.jpablo.typeexplorer.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.TextDocumentsWithSource
import org.jpablo.typeexplorer.inheritance.InheritanceDiagram

object InheritanceTree:

  def buildTree($classes: EventStream[InheritanceDiagram]): EventStream[List[HtmlElement]] =
    for diagram <- $classes yield
      for ns <- diagram.namespaces.sortBy(_.displayName) yield
        div(
          ns.displayName
        )

end InheritanceTree
