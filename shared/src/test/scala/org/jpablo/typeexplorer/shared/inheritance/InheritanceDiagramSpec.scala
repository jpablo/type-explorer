package org.jpablo.typeexplorer.shared.inheritance

import zio.test.*
import zio.test.Assertion.*

import org.jpablo.typeexplorer.shared.models

object InheritanceDiagramSpec extends ZIOSpecDefault:

  // sbt> testOnly org.jpablo.typeexplorer.shared.inheritance.*

  def makeClass(name: String) =
    models.Namespace(models.Symbol(name), name, models.NamespaceKind.Class)

  val base0  = makeClass("base0")
  val base1  = makeClass("base1")
  val base2  = makeClass("base2")
  val classA = makeClass("classA")
  val classB = makeClass("classB")
  val classC = makeClass("classC")

  /*
          ┌─────┐
          │base0│
          └─────┘
             ▲
     ┌───────┴──────┐
     │              │
  ┌───────┐      ┌───────┐
  │ base1 │      │ base2 │
  └───────┘      └───────┘
      ▲              ▲
      └───────┬──────┘
              │
          ┌──────┐
          │classA│
          └──────┘
              ▲
        ┌─────┴─────┐
        │           │
    ┌──────┐    ┌──────┐
    │classB│    │classC│
    └──────┘    └──────┘
   */

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

  def spec =
    suite("Related symbols spec")(

      test("unfold"):
        val ss = diagram.unfold(Set(classB.symbol), diagram.directParents)
        assertTrue(ss == Set(classA, base1, base2, base0).map(_.symbol))
      ,

      test("subdiagram - single symbol"):
        val filtered = diagram.subdiagram(Set(classA.symbol))
        val expected = InheritanceDiagram(arrows = Set(), namespaces = Set(classA) )
        assertTrue(filtered == expected)
      ,

      test("subdiagram - multiple symbols"):
        val filtered = diagram.subdiagram(Set(classA, classB, classC).map(_.symbol))
        val expected =
          InheritanceDiagram(
            arrows = Set((classB.symbol, classA.symbol), (classC.symbol, classA.symbol)),
            namespaces = Set(classA, classB, classC)
          )
        assertTrue(filtered == expected)
      ,

      test("Find all parents - simple case"):
        val filtered = diagram.parentsOf(classA.symbol)

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
      ,

      test("Find all parents"):
        val filtered = diagram.parentsOf(classB.symbol)

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
      ,

      test("parentsOfAll"):
        val base3  = makeClass("base3")
        val base4  = makeClass("base4")
        val diagram2 = InheritanceDiagram(
          Set(base3.symbol -> base4.symbol),
          Set(base3, base4)
        )
        val diagram3 = diagram ++ diagram2
        val result = diagram3.parentsOfAll(Set(base2.symbol, base3.symbol)).symbols
        val expected = Set(
          base2.symbol, base0.symbol,
          base3.symbol, base4.symbol
        )

        assertTrue(result == expected)
      ,

      test("childrenOfAll"):
        val base3  = makeClass("base3")
        val base4  = makeClass("base4")
        val diagram2 = InheritanceDiagram(
          Set(base3.symbol -> base4.symbol),
          Set(base3, base4)
        )
        val diagram3 = diagram ++ diagram2
        val result = diagram3.childrenOfAll(Set(classA.symbol, base4.symbol)).symbols
        val expected = Set(
          classA.symbol, classB.symbol, classC.symbol,
          base3.symbol, base4.symbol
        )

        assertTrue(result == expected)
    )

