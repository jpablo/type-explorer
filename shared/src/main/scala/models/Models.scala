package models

import io.circe.*, io.circe.generic.semiauto.*

case class Package(name: String)

object Package:
  given Codec[Package] = deriveCodec



case class Type(
  name     : String,
  `package`: Option[Package] = None,
  methods  : List[Method] = List.empty
)

object Type:
  given Codec[Type] = deriveCodec



case class Method(name: String, returnType: Option[Type] = None)

object Method:
  given Codec[Method] = deriveCodec
