package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.Project
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom.document

object MainJS:

  def main(args: Array[String]): Unit =
    val project: Project = Project.build(fetchInheritanceDiagram)

    val documents = fetchDocuments(project.basePaths)
    val inheritanceSvgDiagram = fetchInheritanceSVGDiagram(project).startWith(InheritanceSvgDiagram.empty)

    val app = TopLevel(project, inheritanceSvgDiagram, documents)

    render(document.querySelector("#app"), app)
