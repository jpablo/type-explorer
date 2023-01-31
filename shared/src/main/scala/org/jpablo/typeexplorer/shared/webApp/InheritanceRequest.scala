package org.jpablo.typeexplorer.shared.webApp


import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance
import zio.json.*
import org.jpablo.typeexplorer.shared.models.Symbol

case class InheritanceRequest(
  paths  : List[String],
  symbols: List[(Symbol, Option[PlantumlInheritance.SymbolOptions])],
  options: PlantumlInheritance.DiagramOptions = PlantumlInheritance.DiagramOptions(),
)

object InheritanceRequest:
  given JsonFieldEncoder[Symbol] = JsonFieldEncoder[String].contramap(_.toString)
  given JsonFieldDecoder[Symbol] = JsonFieldDecoder[String].map(Symbol(_))
  given JsonCodec[InheritanceRequest] = DeriveJsonCodec.gen




