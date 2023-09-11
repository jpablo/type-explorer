package org.jpablo.typeexplorer.shared.inheritance

import zio.test.*
import zio.json.*


object PlantumlInheritanceSpec extends ZIOSpecDefault {

  val json = io.Source.fromInputStream(getClass.getResourceAsStream("/example-inheritance-diagram.json")).mkString.strip
  val diagram = json.fromJson[InheritanceDiagram].toOption.get

  def spec =
    suite("PlantumlInheritance")(
      test("export to plantuml"):
        val uml = diagram.toPlantUML(Map.empty, DiagramOptions(showFields = true))
        val expected = io.Source.fromInputStream(getClass.getResourceAsStream("/example.puml")).mkString.strip

        assertTrue(uml.diagram == expected)
    )
}
