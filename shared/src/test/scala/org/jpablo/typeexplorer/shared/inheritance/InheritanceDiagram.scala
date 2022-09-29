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
    arrows = Set(
      base1.symbol  -> base0.symbol,
      base2.symbol  -> base0.symbol,
      classA.symbol -> base1.symbol,
      classA.symbol -> base2.symbol,
      classB.symbol -> classA.symbol,
      classC.symbol -> classA.symbol,
    ),
    Set(base0, base1, base2, classA, classB, classC)
  )

  def spec = suite("Related symbols spec")(

    test("unfold") {
      val ss = diagram.unfold(classB.symbol, diagram.directParents)
      assertTrue(ss == Set(classA, base1, base2, base0).map(_.symbol))
    },

    test("subdiagram 1") {
      val filtered = diagram.subdiagram(Set(classA.symbol))
      val expected = InheritanceDiagram(arrows = Set(), namespaces = Set(classA) )
      assertTrue(filtered == expected)
    },

    test("subdiagram 2") {
      val filtered = diagram.subdiagram(Set(classA, classB, classC).map(_.symbol))
      val expected = 
        InheritanceDiagram(
          arrows = Set((classB.symbol, classA.symbol), (classC.symbol, classA.symbol)),
          namespaces = Set(classA, classB, classC)
        )
      assertTrue(filtered == expected)
    },

    test("Find all parents v2 - simple case") {
      val filtered = diagram.allParents(classA.symbol)

      val expected = 
        InheritanceDiagram(
          arrows = Set(
            base1.symbol -> base0.symbol,
            base2.symbol -> base0.symbol,
            classA.symbol -> base1.symbol,
            classA.symbol -> base2.symbol,
          ),
          namespaces = Set(base0, base1, base2, classA)
        )
      assertTrue(filtered == expected)
    },

    test("Find all parents v2") {
      val filtered = diagram.allParents(classB.symbol)

      val expected = 
        InheritanceDiagram(
          arrows = Set(
            base1.symbol -> base0.symbol,
            base2.symbol -> base0.symbol,
            classA.symbol -> base1.symbol,
            classA.symbol -> base2.symbol,
            classB.symbol -> classA.symbol,
          ),
          namespaces = Set(base0, base1, base2, classA, classB)
        )
      assertTrue(filtered == expected)
    },

    // -----------------------------------------

    test("Find all parents - simple case") {
      val related = Set(Related.Parents)
      val filtered = 
        diagram.filterSymbols(List(base1.symbol -> related, base2.symbol -> related))
      
      val filtered2 = diagram.allParents(base1.symbol)

      val expected = 
        InheritanceDiagram(
          arrows = Set(base1.symbol -> base0.symbol, base2.symbol -> base0.symbol),
          namespaces = Set(base0, base1, base2)
        )
      assertTrue(filtered == expected)
    },

    test("Find all parents") {
      val related = Set(Related.Parents)
      val filtered = 
        diagram.filterSymbols(List(classB.symbol -> related, classC.symbol -> related))
      val expected = diagram

      assertTrue(filtered == expected)
    },

    test("Find all children - simple case") {
      val related = Set(Related.Children)
      val filtered = 
        diagram.filterSymbols(List(classA.symbol -> related))

      val expected = 
        InheritanceDiagram(
          arrows = Set(classB.symbol -> classA.symbol, classC.symbol -> classA.symbol),
          namespaces = Set(classA, classB, classC)
        )
      assertTrue(filtered == expected)
    },

    test("Find all children") {
      val related = Set(Related.Children)
      val filtered = 
        diagram.filterSymbols(List(base0.symbol -> related))

      val expected = diagram

      assertTrue(filtered == expected)
    }

  )
}

