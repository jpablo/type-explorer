package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import zio.*
import zhttp.http.*
import zhttp.service.Server
import io.circe.syntax.*
import io.circe.parser.*
import semanticdb.{All, ClassesList}
import scalapb.json4s.JsonFormat

import java.nio.file.Paths
import java.net.URI
import io.circe.Json
import org.json4s.*
import org.json4s.JsonDSL.*
import org.json4s.native.JsonMethods.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

object WebApp extends ZIOAppDefault {

  val allowCors = "Access-Control-Allow-Origin" -> "*"

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  val app = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb" =>
      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          val docs = All.scan(Paths.get(h, t *))
          Response
            .json(write(docs.map((p, d) => (p.toString, d))))
            .addHeader(allowCors)

        case _ =>
          Response.status(Status.BadRequest)

    case req @ Method.GET -> !! / "classes" =>
      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          val paths = Paths.get(h, t*)
          val namespaces = ClassesList.scan(paths)
          Response.json(namespaces.asJson.toString)
            .addHeader(allowCors)

        case _ =>
          Response.status(Status.BadRequest)

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
