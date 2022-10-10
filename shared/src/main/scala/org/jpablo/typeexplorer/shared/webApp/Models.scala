package org.jpablo.typeexplorer.shared.webApp


import zio.json.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.Related

case class InheritanceReq(
  paths  : List[String],
  symbols: Set[(models.Symbol, Set[Related])]
)

object InheritanceReq:
  given JsonEncoder[InheritanceReq] = DeriveJsonEncoder.gen
  given JsonDecoder[InheritanceReq] = DeriveJsonDecoder.gen

