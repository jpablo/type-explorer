package org.jpablo.typeexplorer.shared.webApp


import zio.json.*
import org.jpablo.typeexplorer.shared.models.Symbol

case class InheritanceRequest(
  paths  : List[String],
  symbols: Set[Symbol],
  options: InheritanceRequest.Config = InheritanceRequest.Config(),
)

object InheritanceRequest:
  case class Config(
    fields    : Boolean = false,
    signatures: Boolean = false,
  )
  given JsonCodec[Config] = DeriveJsonCodec.gen
  given JsonCodec[InheritanceRequest] = DeriveJsonCodec.gen




