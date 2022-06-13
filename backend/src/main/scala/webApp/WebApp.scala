package webApp

import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import org.jpablo.typeexplorer.TextDocumentWithSource
import zio.*
import zhttp.http.*
import zhttp.service.Server
import semanticdb.{All, ClassesList}

import scala.meta.internal.semanticdb.TextDocuments
import java.net.URI
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig

import java.nio.file
import util.Operators.*

object WebApp extends ZIOAppDefault {

  // ----------
  // endpoints
  // ----------
  val app = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb" =>
      (req |> getPath |> readTextDocuments)
        .map(_.toByteArray)
        .map(Chunk.fromArray)
        .map(HttpData.fromChunk)
        .map(d => Response(data = d))
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.json" =>
      (req |> getPath |> readTextDocuments)
        .map(write)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.textproto" =>
      (req |> getPath |> readTextDocuments)
        .map(_.toProtoString)
        .map(Response.text)
        .getOrElse(badRequest)

//    case req @ Method.GET -> !! / "classes" =>
//      (req |> getPath |> combinePaths)
//        .map(ClassesList.scan)
//        .map(_.asJson.toString)
//        .map(Response.json)
//        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "inheritance" =>
      (req |> getPath |> readTextDocuments)
        .map(PlantumlInheritance.fromTextDocuments)
        .map(PlantumlInheritance.renderDiagram("laminar", _))
        .map(Response.text)
        .map(_.withContentType("image/svg+xml"))
        .getOrElse(badRequest)

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, app)

  // -----------------
  // helper functions
  // -----------------

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  def readTextDocuments(path: Option[List[String]]) =
    for p <- combinePaths(path) yield
      All.scan(p).flatMap(_._2.documents.toList) |> TextDocuments.apply

  def combinePaths(path: Option[List[String]]): Option[file.Path] =
    for case h :: t <- path if h.nonEmpty yield
      file.Paths.get(h, t*)

  def getPath(req: Request) =
    req.url.queryParams.get("path")

  lazy val corsConfig =
    CorsConfig(allowedOrigins = _ => true)

  lazy val badRequest =
    Response.status(Status.BadRequest)

}
