package fileTree

import zio.prelude.NonEmptyList

type Name = String

enum FileTree[+A]:
  case Directory(name: Name, contents: List[FileTree[A]])
  case File(name: Name, data: A)
  def name: Name

object FileTree:

  // segments = List[Name] | NonEmptyList[Name]
  type LeafName = String

  type Path[A]         = (A, LeafName, List[Name])
  type NonEmptyPath[A] = (A, LeafName, NonEmptyList[Name])


  def build[A](files: List[A])(buildPath: A => Path[A]): List[FileTree[A]] =
    fromPaths(files.map(buildPath))


  // -----------------------

  private def fromPaths[A](paths: List[Path[A]]): List[FileTree[A]] =
    val leafNodes        = paths.collect { case (a, leafName, Nil)    => File(leafName, a) }
    val nonEmptySegments = paths.collect { case (a, leafName, h :: t) => (a, leafName, NonEmptyList(h, t*)) }

    val byFirstSegment:  List[(Name, List[Path[A]])] =
      nonEmptySegments
        .groupBy { case (_, _, segments) => segments.head }
        .transform((_, group) => dropHeads(group))
        .toList

    val branchNodes =
      for (groupName, pathsGroup) <- byFirstSegment yield
        joinEmptyDirectories(groupName, fromPaths(pathsGroup))

    branchNodes ++ leafNodes


  private def dropHeads[A](paths: List[NonEmptyPath[A]]): List[Path[A]] =
    for (file, leafName, segments) <- paths yield
      (file, leafName, segments.tail)


  private def joinEmptyDirectories[A](groupName: Name, trees: List[FileTree[A]]): FileTree[A] =
    trees match
      case (d: Directory[A]) :: Nil =>
        Directory(groupName + "/" + d.name, d.contents)
      case _ =>
        Directory(groupName, trees)

