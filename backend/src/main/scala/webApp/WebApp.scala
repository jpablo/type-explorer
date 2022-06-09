package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import zio.*
import zhttp.http.*
import zhttp.service.Server
import io.circe.syntax.*
import io.circe.parser.*
import semanticdb.{All, ClassesList}

import scala.meta.internal.semanticdb.TextDocuments
import scalapb.json4s.JsonFormat

import java.net.URI
import io.circe.Json
import org.json4s.*
import org.json4s.JsonDSL.*
import org.json4s.native.JsonMethods.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import java.nio.file
import util.Operators.*

object WebApp extends ZIOAppDefault {

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  def readTextDocuments(path: Option[List[String]]): Option[TextDocuments] =
    for
      case h :: t <- path if h.nonEmpty
    yield
      (file.Paths.get(h, t *) |> All.scan flatMap (_._2.documents)) |> TextDocuments.apply

  val corsConfig =
    CorsConfig(allowedOrigins = _ => true)

  def getPath(req: Request) = req.url.queryParams.get("path")

  val badRequest = Response.status(Status.BadRequest)
  // ----------
  // endpoints
  // ----------
  val app = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb.json" =>
      (req |> getPath |> readTextDocuments)
        .map(_ |> write |> Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb" =>
      (req |> getPath |> readTextDocuments)
        .map(_ |> (_.toByteArray) |> Chunk.fromArray |> HttpData.fromChunk)
        .map(d => Response(data = d))
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.textproto" =>
      (req |> getPath |> readTextDocuments)
        .map(_ |> (_.toProtoString) |> Response.text)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "classes" =>
      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          val paths = file.Paths.get(h, t*)
          val namespaces = ClassesList.scan(paths)
          Response.json(namespaces.asJson.toString)
        case _ =>
          badRequest

    case req @ Method.GET -> !! / "inheritance" =>
      val response =
        for
          textDocuments <- req |> getPath |> readTextDocuments
          plantUmlText = PlantumlInheritance.toDiagram(textDocuments)
          svgText = PlantumlInheritance.renderDiagram("laminar", plantUmlText)
        yield
          Response.text(svgText).withContentType("image/svg+xml")

      response.getOrElse(badRequest)

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, app)
}
