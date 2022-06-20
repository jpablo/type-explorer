package webApp


import backends.plantuml.PlantumlInheritance
import inheritance.InheritanceExamples
import java.net.URI
import java.nio.file
import org.jpablo.typeexplorer.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import scala.meta.internal.semanticdb.TextDocuments
import semanticdb.{All, ClassesList}
import util.Operators.*
import zhttp.http.*
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import zio.*
import zio.json.*

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

    case req @ Method.GET -> !! / "classes" =>
      (req |> getPath |> readTextDocuments)
        .map(docs => toTextDocuments(docs))
        .map(ClassesList.fromTextDocuments)
        .map(_.toJson)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "inheritance" =>
      (req |> getPath |> readTextDocuments)
        .map(toTextDocuments)
        .map(ClassesList.fromTextDocuments)
        .map(PlantumlInheritance.toDiagramString)
        .map(PlantumlInheritance.renderDiagramString("laminar", _))
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

  def toTextDocuments(docs: TextDocumentsWithSourceSeq): TextDocuments =
    docs.documentsWithSource.flatMap(_.documents) |> TextDocuments.apply

  def readTextDocuments(path: Option[List[String]]): Option[TextDocumentsWithSourceSeq] =
    for p <- combinePaths(path) yield
      TextDocumentsWithSourceSeq(
        All.scan(p).map { case (path, d) =>
          TextDocumentsWithSource(path.toString).withDocuments(d.documents)
        }
      )

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
