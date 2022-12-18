package org.jpablo.typeexplorer.shared.tree

import zio.test.*

object TreeSpec extends ZIOSpecDefault:
  import Tree.*
  override def spec = suite("Tree Spec") (
    test("single leaf") {
      val tree = fromPaths(
        List(
          (List("a", "b", "c"), "d", 1)
        )
      )
      val expected =
        List(
          Node("a/b/c", List("a", "b", "c"), List(Leaf("d", 1)))
        )
      assertTrue(expected == tree)
    },

    test("multiple leaves with common sub path") {
      val tree = fromPaths(
        List(
          (List("a", "b", "c"), "d", 1),
          (List("a", "b", "c"), "e", 2),
          (List("a", "b", "f"), "g", 3),
        )
      )

      val expected =
        List(
          Node(
            "a/b",
            List("a", "b"),
            List(
              Node(
                "c",
                List("a", "b", "c"),
                List(Leaf("d", 1), Leaf("e", 2))
              ),
              Node(
                "f",
                List("a", "b", "f"),
                List(Leaf("g", 3))
              )
            )
          )
        )
      assertTrue(expected == tree)
    }
  )
