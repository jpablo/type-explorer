package inheritance

case class Package(name: String)

case class Method(name: String)

case class Type(name: String, `package`: Option[Package] = None, methods: List[Method] = List.empty)

type Parents = Map[Type, List[Type]]

type Children = Map[Type, List[Type]]

case class InheritanceDiagram(
  types   : List[Type],
  parents : Parents,
  children: Children,
)
