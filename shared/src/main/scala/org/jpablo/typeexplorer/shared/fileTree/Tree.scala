package org.jpablo.typeexplorer.shared.fileTree

import zio.prelude.NonEmptyList
import zio.json.*
import com.softwaremill.quicklens.*




enum Tree[+A]:
  case Node(label: Tree.Label, children: List[Tree[A]])
  case Leaf(label: Tree.Label, data: A)

  def label: Tree.Label

object Tree:
  type Label = String
  given [A: JsonCodec]: JsonCodec[Tree[A]] = DeriveJsonCodec.gen

  type LeafWithPath[A] = (List[Label], Label, A)

  def fromPaths[A](paths: List[LeafWithPath[A]], sep: String = "/"): List[Tree[A]] =
    val leaves        = paths.collect { case (Nil,    label, data) => Leaf(label, data) }
    val nonEmptyPaths = paths.collect { case (h :: t, label, data) => (NonEmptyList(h, t*), label, data) }

    val leafGroups: List[(Label, List[LeafWithPath[A]])] =
      nonEmptyPaths
        .groupBy((path, _, _) => path.head)
        .transform((_, group) => group.map(pathTail))
        .toList

    val nodes =
      for (groupLabel, groupPaths) <- leafGroups yield
        val subtrees = fromPaths(groupPaths, sep)
        node(groupLabel, subtrees, sep)

    nodes.sortBy(_.label) ++ leaves.sortBy(_.label)


  private def pathTail[B](path: (NonEmptyList[Label], Label, B)): LeafWithPath[B] =
    path.copy(_1 = path._1.tail)

  private def node[A](label: Label, trees: List[Tree[A]], sep: String): Tree[A] =
    trees match
      case List(d: Node[A]) => d.modify(_.label)(label + sep + _)
      case _                   => Node(label, trees)

