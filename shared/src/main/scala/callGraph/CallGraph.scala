package callGraph

import models.Method

case class CallGraph(
  pairs: List[(Method, Method)],
  methods: List[Method] = List.empty
)
