package org.jpablo.typeexplorer.shared.tree

import zio.prelude.NonEmptyList
import zio.json.*
import com.softwaremill.quicklens.*

enum Tree[+A] derives JsonCodec:
  case Node(label: Tree.Label, path: List[Tree.Label], children: List[Tree[A]])
  case Leaf(label: Tree.Label, data: A)

  def label: Tree.Label

object Tree:
  type Label = String
  type LeafWithPath[A] = (List[Label], Label, A)

  def fromPaths[A](paths: List[LeafWithPath[A]], sep: String = "/", prefix: List[Tree.Label] = List.empty): List[Tree[A]] =
    val leaves        = paths.collect { case (Nil,    label, data) => Leaf(label, data) }
    val nonEmptyPaths = paths.collect { case (h :: t, label, data) => (NonEmptyList(h, t*), label, data) }

    val leafGroups: List[(Label, List[LeafWithPath[A]])] =
      nonEmptyPaths
        .groupBy((path, _, _) => path.head)
        .transform((_, group) => group.map(pathTail))
        .toList

    val nodes =
      for (groupLabel, groupPaths) <- leafGroups yield
        val prefix1 = prefix :+ groupLabel
        val subtrees = fromPaths(groupPaths, sep, prefix1)
        node(groupLabel, subtrees, prefix1, sep)

    nodes.sortBy(_.label) ++ leaves.sortBy(_.label)


  private def pathTail[B](path: (NonEmptyList[Label], Label, B)): LeafWithPath[B] =
    path.copy(_1 = path._1.tail)

  private def node[A](label: Label, trees: List[Tree[A]], path: List[Tree.Label], sep: String): Tree[A] =
    trees match
      case List(d: Node[A]) => d.modify(_.label)(label + sep + _)
      case _                => Node(label, path, trees)

