package org.jpablo.typeexplorer.shared.inheritance

import zio.test.*
import zio.json.*


object PlantumlInheritanceSpec extends ZIOSpecDefault {

  val json = io.Source.fromInputStream(getClass.getResourceAsStream("/example.json")).mkString.strip
  val g: org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram = json.fromJson[org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram].toOption.get

  def spec =
    suite("PlantumlInheritance")(
      test("export to plantuml"):
        val uml = g.toPlantUML(Map.empty, DiagramOptions(showFields = true))
        val expected = io.Source.fromInputStream(getClass.getResourceAsStream("/example.puml")).mkString.strip

        assertTrue(uml.diagram == expected)
    )
}
