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

object WebApp extends ZIOAppDefault {

  val allowCors = "Access-Control-Allow-Origin" -> "*"

  given formats: Formats =
    Serialization.formats(NoTypeHints)


  def buildDocs(path: file.Path): TextDocuments =
    val docsWithPaths = All.scan(path)
    TextDocuments(docsWithPaths.flatMap(_._2.documents))


  val corsConfig =
    CorsConfig(allowedOrigins = _ => true)


  val app = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb.json" =>
      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          Response.json(write(buildDocs(file.Paths.get(h, t*))))
        case _ =>
          Response.json("[]")

    case req @ Method.GET -> !! / "semanticdb" =>

      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          val docs = buildDocs(file.Paths.get(h, t*))
          Response(data = HttpData.fromChunk(Chunk.fromArray(docs.toByteArray)))
        case _ =>
          Response.text("")

    case req @ Method.GET -> !! / "semanticdb.textproto" =>

      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          Response.text(buildDocs(file.Paths.get(h, t *)).toProtoString)
        case _ =>
          Response.text("")

    case req @ Method.GET -> !! / "classes" =>
      req.url.queryParams.get("path") match
        case Some(h :: t) if h.nonEmpty =>
          val paths = file.Paths.get(h, t*)
          val namespaces = ClassesList.scan(paths)
          Response.json(namespaces.asJson.toString)

        case _ =>
          Response.status(Status.BadRequest)

    case Method.GET -> !! / "inheritance" =>
      val plantUmlText = PlantumlInheritance.toDiagram(InheritanceExamples.laminar)
      val svgText = PlantumlInheritance.renderDiagram("laminar", plantUmlText)
      Response
        .text(svgText)
        .withContentType("image/svg+xml")

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, app)
}
