package org.jpablo.typeexplorer.shared.inheritance

import zio.test.*
import zio.test.Assertion.*

import org.jpablo.typeexplorer.shared.models

object InheritanceDiagramSpec extends ZIOSpecDefault {

  // sbt> testOnly org.jpablo.typeexplorer.shared.inheritance.*

  def makeClass (name: String) = 
    models.Namespace(models.Symbol(name), name, models.NamespaceKind.Class)

  val base0  = makeClass("base0")
  val base1  = makeClass("base1")
  val base2  = makeClass("base2")
  val classA = makeClass("classA")
  val classB = makeClass("classB")
  val classC = makeClass("classC")


  val diagram = InheritanceDiagram(
    arrows = List(
      base1.symbol  -> base0.symbol,
      base2.symbol  -> base0.symbol,
      classA.symbol -> base1.symbol,
      classA.symbol -> base2.symbol,
      classB.symbol -> classA.symbol,
      classC.symbol -> classA.symbol,
    ),
    List(base0, base1, base2, classA, classB, classC)
  )

  def spec = suite("Related symbols spec")(
    test("Find all parents - simple case") {
      val related = Set(Related.Parents)
      val filtered = 
        diagram.filterSymbols(List(base1.symbol -> related, base2.symbol -> related))

      val expected = 
        InheritanceDiagram(
          arrows = List(base1.symbol -> base0.symbol, base2.symbol -> base0.symbol),
          namespaces = List(base0, base1, base2)
        )
      assertTrue(filtered.arrows.toSet == expected.arrows.toSet)
      assertTrue(filtered.namespaces.toSet == expected.namespaces.toSet)
    },

    test("Find all parents") {
      val related = Set(Related.Parents)
      val filtered = 
        diagram.filterSymbols(List(classB.symbol -> related, classC.symbol -> related))

      val expected = diagram

      assertTrue(filtered.arrows.toSet == expected.arrows.toSet)
      assertTrue(filtered.namespaces.toSet == expected.namespaces.toSet)
    },

    test("Find all children - simple case") {
      val related = Set(Related.Children)
      val filtered = 
        diagram.filterSymbols(List(classA.symbol -> related))

      val expected = 
        InheritanceDiagram(
          arrows = List(classB.symbol -> classA.symbol, classC.symbol -> classA.symbol),
          namespaces = List(classA, classB, classC)
        )
      assertTrue(filtered.arrows.toSet == expected.arrows.toSet)
      assertTrue(filtered.namespaces.toSet == expected.namespaces.toSet)
    },

    test("Find all children") {
      val related = Set(Related.Children)
      val filtered = 
        diagram.filterSymbols(List(base0.symbol -> related))

      val expected = diagram

      assertTrue(filtered.arrows.toSet == expected.arrows.toSet)
      assertTrue(filtered.namespaces.toSet == expected.namespaces.toSet)
    }

  )
}

