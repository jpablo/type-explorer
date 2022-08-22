package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.appFooter
import org.jpablo.typeexplorer.ui.app.components.tabs.tabsArea
import io.laminext.syntax.core.*

object TopLevel {

  val $newDiagramType = new EventBus[DiagramType]
  val $selectedUri    = new EventBus[String]
  val $projectPath    = storedString("projectPath", initial = "")
  val $documents      = fetchDocuments($projectPath.signal)
  val $inheritance    = fetchSVGDiagram($projectPath.signal.map(path => (DiagramType.Inheritance, path)))
  val $classes        = fetchClasses($projectPath.signal)

  // val $fullProjectPath =
  //   $selectedUri.events.toSignal("").flatMap(uri => $projectPath.signal.map(p => p + uri))

  def topLevel: Div =
    div(
      idAttr := "te-toplevel",
      appHeader($newDiagramType, $projectPath),
      tabsArea(
        $projectPath.signal,
        $documents,
        $inheritance,
        $classes,
        $selectedUri
      ),
      appFooter
    )

}
