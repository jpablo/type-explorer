package models

import io.circe.*, io.circe.generic.semiauto.*

case class Package(name: String)

object Package:
  given Encoder[Package] = deriveEncoder



case class Type(
  name     : String,
  `package`: Option[Package] = None,
  methods  : List[Method] = List.empty
)

object Type:
  given Encoder[Type] = deriveEncoder



case class Method(name: String, returnType: Option[Type] = None)

object Method:
  given Encoder[Method] = deriveEncoder
