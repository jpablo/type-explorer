package app.components.tabs.semanticDBTab

type Name = String

enum FileTree[+A]:
  case Directory(name: Name, contents: List[FileTree[A]])
  case File(name: Name, data: A)
  def name: Name

object FileTree:

  type Segment[A] = (A, List[Name])

  def build[A](files: List[A])(getPath: A => String, splitBy: String = "/"): List[FileTree[A]] =
    fromSegments(buildSegments(files, getPath, splitBy), splitBy)


  // -----------------------

  private def buildSegments[A](files: List[A], getPath: A => String, splitBy: String): List[Segment[A]] =
    for
      file    <- files
      path     = getPath(file)
      segments = (path split splitBy).toList.filter(_.nonEmpty)
      segmentsWithRoot =
        if path startsWith splitBy
        then (splitBy + segments.head) :: segments.tail
        else segments
    yield
      file -> segmentsWithRoot


  private def fromSegments[A](segments: List[Segment[A]], splitBy: String): List[FileTree[A]] =
    val byCommonName: Map[Name, List[Segment[A]]] =
      segments
        .groupBy(_._2.head)
        .transform((_, nextSegments) => dropHeads(nextSegments))

    for (name, nextSegments) <- byCommonName.toList yield nextSegments match
      case (doc -> Nil) :: Nil => File(name, doc)
      case _ => fromSegments(nextSegments, splitBy) match
        // join directories with a single directory child
        case Directory(nextName, nextContents) :: Nil => Directory(name + splitBy + nextName, nextContents)
        case contents                                 => Directory(name, contents)


  private def dropHeads[A](segments: List[Segment[A]]): List[Segment[A]] =
    for case (file, _ :: next) <- segments yield
      file -> next
