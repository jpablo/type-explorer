package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.{ProjectBuilder, Project}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom.document

object MainJS:

  def main(args: Array[String]): Unit =

    val currentProject: Project = ProjectBuilder.build(fetchInheritanceDiagram)

    val documents = fetchDocuments(currentProject.basePaths)
    val inheritanceSvgDiagram = fetchInheritanceSVGDiagram(currentProject).startWith(InheritanceSvgDiagram.empty)

    val app = TopLevel(currentProject, inheritanceSvgDiagram, documents)

    render(document.querySelector("#app"), app)
