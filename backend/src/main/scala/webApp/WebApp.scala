package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import zio.*
import zhttp.http.*
import zhttp.service.Server
import io.circe.syntax.*
import semanticdb.ClassesList
import java.nio.file.Paths
import java.net.URI

object WebApp extends ZIOAppDefault {

  val allowCors = "Access-Control-Allow-Origin" -> "*"

  val app = Http.collect[Request] {

    case Method.GET -> !! / "classes" / path =>
      val paths = 
        Paths.get (new URI (s"file://$path"))      
      val namespaces = ClassesList.scan (paths)
      Response.json(namespaces.asJson.toString)
        .addHeader(allowCors)

    case Method.GET -> !! / "inheritance" =>
      val plantUmlText = PlantumlInheritance.toDiagram(InheritanceExamples.laminar)
      val svgText = PlantumlInheritance.renderDiagram("laminar", plantUmlText)

      Response.text(svgText)
        .withContentType("image/svg+xml")
        .addHeader(allowCors)
  }

  val run =
    Server.start(8090, app)
}
