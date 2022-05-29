package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import zio.*
import zhttp.http.*
import zhttp.service.Server

object WebApp extends ZIOAppDefault {

  val app = Http.collect[Request] {
    case Method.GET -> !! / "inheritance" =>
      val diagram = PlantumlInheritance.toDiagram(InheritanceExamples.laminar)
      println("-----------------")
      println(diagram)
      println("-----------------")
      val svg = PlantumlInheritance.renderDiagram("laminar", diagram)
      Response.text(svg).withContentType("image/svg+xml").addHeader("Access-Control-Allow-Origin" -> "*")
  }

  val run =
    Server.start(8090, app)
}
