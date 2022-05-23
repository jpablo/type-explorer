package models

case class Package(name: String)

case class Type(
  name     : String,
  `package`: Option[Package] = None,
  methods  : List[Method] = List.empty
)

case class Method(name: String, returnType: Option[Type] = None)
