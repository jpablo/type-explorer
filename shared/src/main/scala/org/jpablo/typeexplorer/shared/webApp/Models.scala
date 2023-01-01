package org.jpablo.typeexplorer.shared.webApp


import zio.json.*
import org.jpablo.typeexplorer.shared.models.Symbol

case class InheritanceReq(
  paths  : List[String],
  symbols: Set[Symbol],
  options: InheritanceReq.Config = InheritanceReq.Config(),
)

object InheritanceReq:

  case class Config(
    fields    : Boolean = false,
    signatures: Boolean = false,
  )

  given JsonCodec[Config] = DeriveJsonCodec.gen
  given JsonCodec[InheritanceReq] = DeriveJsonCodec.gen




