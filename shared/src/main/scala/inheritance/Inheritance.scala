package inheritance

case class Package(name: String)

case class Method(name: String, output: Option[Type] = None)

case class Type(
  name     : String,
  `package`: Option[Package] = None,
  methods  : List[Method] = List.empty
)

type Parents = Map[Type, List[Type]]

type Children = Map[Type, List[Type]]

case class InheritanceDiagram(
  pairs   : List[(Type, Type)],
  types   : List[Type] = List.empty,
  parents : Parents = Map.empty,
  children: Children = Map.empty,
)
