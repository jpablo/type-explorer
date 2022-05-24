package callGraph

import models.*

case class CallGraph(
  pairs: List[(Method, Method)],
  namesSpaces: List[Type] = List.empty
)
