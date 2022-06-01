package inheritance

import models.*
import io.circe.*, io.circe.generic.semiauto.*

case class InheritanceDiagram(
  pairs   : List[(Type, Type)],
  types   : List[Type] = List.empty
)

object InheritanceDiagram:
  given Encoder[InheritanceDiagram] = deriveEncoder
