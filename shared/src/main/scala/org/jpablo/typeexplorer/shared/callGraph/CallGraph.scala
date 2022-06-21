package org.jpablo.typeexplorer.shared.callGraph

import org.jpablo.typeexplorer.shared.models.{Method, Namespace}

case class CallGraph(
  pairs: List[(Method, Method)],
  namesSpaces: List[Namespace] = List.empty
)
