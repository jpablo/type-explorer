package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.appFooter
import org.jpablo.typeexplorer.ui.app.components.tabs.tabsArea
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.Path

object TopLevel {

  val $newDiagramType = EventBus[DiagramType]
  val $selectedUri    = EventBus[Path]
  val $selectedSymbol = EventBus[models.Symbol]
  val projectPath     = storedString("projectPath", initial = "")
  val $projectPath    = projectPath.signal.map(Path.apply)
  val $documents      = fetchDocuments($projectPath)
  val $classes        = fetchClasses($projectPath)
  val $inheritance    = $selectedSymbol.events
    .foldLeft(Set.empty[models.Symbol])((ss, s) => if ss contains s then ss - s else ss + s)
    .flatMap(ss => fetchInheritanceSVGDiagram($projectPath)(ss, None))


  def topLevel: Div =
    div(
      idAttr := "te-toplevel",
      appHeader($newDiagramType, projectPath),
      tabsArea(
        $projectPath,
        $documents,
        $inheritance,
        $classes,
        $selectedSymbol,
        $selectedUri
      ),
      appFooter
    )

}
