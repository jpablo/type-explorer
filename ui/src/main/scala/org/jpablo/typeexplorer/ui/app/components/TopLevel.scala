package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.appFooter
import org.jpablo.typeexplorer.ui.app.components.tabs.tabsArea

object TopLevel {

  val $newDiagramType = new EventBus[DiagramType]
  val $projectPath    = Var[String]("/Users/jpablo/proyectos/playground/type-explorer")
  var $documents      = fetchDocuments($projectPath.signal)
  var $inheritance    = fetchSVGDiagram($projectPath.signal.map(path => (DiagramType.Inheritance, path)))
  val $classes        = fetchClasses($projectPath.signal)

  def topLevel: Div =
    div(
      idAttr := "te-toplevel",
      appHeader($newDiagramType, $projectPath),
      tabsArea(
        $documents,
        $inheritance,
        $classes
      ),
      appFooter
    )

}
