package org.jpablo.typeexplorer.shared.tree

import zio.prelude.NonEmptyList
import zio.json.*
import com.softwaremill.quicklens.*

enum Tree[+A] derives JsonCodec:
  case Branch(label: Tree.Label, path: List[Tree.Label], override val children: List[Tree[A]])
  case Leaf(label: Tree.Label, data: A)

  def children: List[Tree[A]] = this match
    case b: Branch[a] => b.children
    case _: Leaf[l]   => Nil

  def label: Tree.Label

object Tree:
  type Label = String
  type LeafWithPath[A] = (List[Label], Label, A)

  def fromPaths[A](paths: List[LeafWithPath[A]], sep: String = "/", prefix: List[Tree.Label] = List.empty): Tree[A] =
    val leaves = paths.collect { case (Nil, label, data) => Leaf(label, data) }
    val nonEmptyPaths = paths.collect { case (h :: t, label, data) => (NonEmptyList(h, t*), label, data) }

    val leafGroups: List[(Label, List[LeafWithPath[A]])] =
      nonEmptyPaths
        .groupBy((path, _, _) => path.head)
        .transform((_, group) => group.map(pathTail))
        .toList

    val nodes =
      for (groupLabel, groupPaths) <- leafGroups yield
        val prefix1 = prefix :+ groupLabel
        val subtrees = fromPaths(groupPaths, sep, prefix1).children
        node(groupLabel, subtrees, prefix1, sep)

    Tree.Branch(
      label    = prefix.mkString(sep),
      path     = prefix,
      children = nodes.sortBy(_.label) ++ leaves.sortBy(_.label)
    )

  private def pathTail[B](path: (NonEmptyList[Label], Label, B)): LeafWithPath[B] =
    path.copy(_1 = path._1.tail)

  private def node[A](label: Label, trees: List[Tree[A]], path: List[Tree.Label], sep: String): Tree[A] =
    trees match
      case List(d: Branch[A]) => d.modify(_.label)(label + sep + _)
      case _                  => Branch(label, path, trees)
