package inheritance

import models.*


case class InheritanceDiagram(
  pairs   : List[(Type, Type)],
  types   : List[Type] = List.empty
)
