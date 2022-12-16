package org.jpablo.typeexplorer.backend.webApp

import org.jpablo.typeexplorer.backend.backends.plantuml.toSVG
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples, Related}
import org.jpablo.typeexplorer.shared.inheritance.{PlantUML, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.utils.*
import org.jpablo.typeexplorer.shared.webApp.InheritanceReq

import io.github.arainko.ducktape.*
import java.net.URI
import java.nio.file
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import scala.meta.internal.semanticdb.TextDocuments
import scala.util.Using
import zhttp.http.*
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import zio.*
import zio.json.*
import zio.prelude.AnySyntax
import zio.ZIO.ZIOConstructor


object WebApp extends ZIOAppDefault:

  // ----------
  // endpoints
  // ----------
  val appZ = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / "inheritance" =>

      def createDiagram(ireq: InheritanceReq): Task[PlantUML] =
        for
          docs <- toTask(readTextDocumentsWithSource(Some(ireq.paths)), error = "No path provided")
          diagram = InheritanceDiagram.fromTextDocuments(toTextDocuments(docs))
          opts = ireq.options.to[PlantumlInheritance.Options]
          puml = PlantumlInheritance.fromInheritanceDiagram(diagram.subdiagram(ireq.symbols), opts)
        yield
          puml

      for
        body <- req.body.asString
        ireq <- toTask(body.fromJson[InheritanceReq])
        puml <- createDiagram(ireq)
        svgText <- puml.toSVG("laminar")
      yield
        Response.text(svgText).withContentType("image/svg+xml")
  } @@ cors(corsConfig)


  val app: Http[Any, Nothing, Request, Response] = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(_.toByteArray)
        .map(arr => Response(body = Body.fromChunk(Chunk.fromArray(arr))))
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.json" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(write)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.textproto" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(_.toProtoString)
        .map(Response.text)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "classes" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(toTextDocuments)
        .map(InheritanceDiagram.fromTextDocuments)
        .map(_.toJson)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "source" =>
      (req |> getPath |> readSource)
        .map(Response.text)
        .map(_.withContentType("text/plain"))
        .getOrElse(badRequest)

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, appZ ++ app)

  // -----------------
  // helper functions
  // -----------------

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  def toTextDocuments(docs: TextDocumentsWithSourceSeq): TextDocuments =
    TextDocuments.apply <|: docs.documentsWithSource.flatMap(_.documents)

  def readTextDocumentsWithSource(path: Option[List[String]]): Option[TextDocumentsWithSourceSeq] =
    for p <- combinePaths(path) yield
      TextDocumentsWithSourceSeq(
        All.scan(p).map { case (path, d) =>
          TextDocumentsWithSource(path.toString).withDocuments(d.documents)
        }
      )

  def readSource(paths: Option[List[String]]): Option[String] =
    for
      path <- paths.toList.flatten.headOption
    yield
      Using.resource(scala.io.Source.fromFile(path)) { bufferedSource =>
        bufferedSource.getLines().mkString("\n")
      }

  def combinePaths(path: Option[List[String]]): Option[file.Path] =
    for case h :: t <- path if h.nonEmpty yield
      file.Paths.get(h, t*)

  def getPath(req: Request): Option[List[String]] =
    getParam(req, "path")

  def getParam(req: Request, name: String) =
    req.url.queryParams.get(name)

  def toTask[A](a: => A, error: String = "")(using ZIOConstructor[Nothing, Any, A], Trace) =
    ZIO.from(a).mapError(e => Throwable(if error.isEmpty then e.toString else error))

  lazy val corsConfig =
    CorsConfig(allowedOrigins = _ => true)

  lazy val badRequest =
    Response.status(Status.BadRequest)
