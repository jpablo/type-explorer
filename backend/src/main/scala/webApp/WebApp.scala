package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import zio.*
import zhttp.http.*
import zhttp.service.Server
import io.circe.syntax.*

object WebApp extends ZIOAppDefault {

  val app = Http.collect[Request] {

    case Method.GET -> !! / "classes" =>
      Response.json(InheritanceExamples.laminar.types.asJson.toString)
        .addHeader("Access-Control-Allow-Origin" -> "*")

    case Method.GET -> !! / "inheritance" =>
      val plantUmlText = PlantumlInheritance.toDiagram(InheritanceExamples.laminar)
      val svgText = PlantumlInheritance.renderDiagram("laminar", plantUmlText)

      Response.text(svgText)
        .withContentType("image/svg+xml")
        .addHeader("Access-Control-Allow-Origin" -> "*")
  }

  val run =
    Server.start(8090, app)
}
