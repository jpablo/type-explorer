package org.jpablo.typeexplorer.callGraph

import org.jpablo.typeexplorer.models.{Method, Namespace}

case class CallGraph(
  pairs: List[(Method, Method)],
  namesSpaces: List[Namespace] = List.empty
)
