package org.jpablo.typeexplorer.shared.webApp


import zio.json.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.shared.inheritance.Related

case class InheritanceReq(
  paths  : List[String],
  symbols: Set[(Symbol, Set[Related])]
)

object InheritanceReq:
  given JsonEncoder[InheritanceReq] = DeriveJsonEncoder.gen
  given JsonDecoder[InheritanceReq] = DeriveJsonDecoder.gen

