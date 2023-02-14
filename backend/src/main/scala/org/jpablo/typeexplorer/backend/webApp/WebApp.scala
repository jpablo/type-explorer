package org.jpablo.typeexplorer.backend.webApp

import org.jpablo.typeexplorer.backend.backends.plantuml.toSVG
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.shared.inheritance.{PlantUML, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.{InheritanceRequest, Routes}
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
import org.jpablo.typeexplorer.shared.webApp.Routes


object WebApp extends ZIOAppDefault:

  // ----------
  // endpoints
  // ----------
  private val appZ = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / Routes.inheritanceDiagram =>

      def createDiagram(ireq: InheritanceRequest): Task[PlantUML] =
        for
          docs <- toTask(readTextDocumentsWithSource(Some(ireq.paths)), error = "No path provided")
          diagram = InheritanceDiagram.fromTextDocuments(toTextDocuments(docs))
          puml = PlantumlInheritance.fromInheritanceDiagram(
            diagram.subdiagram(ireq.symbols.map(_._1).toSet),
            ireq.symbols.toMap,
            ireq.options
          )
        yield
          puml

      for
        body <- req.body.asString
        ireq <- toTask(body.fromJson[InheritanceRequest])
        puml <- createDiagram(ireq)
        svgText <- puml.toSVG("laminar")
      yield
        Response.text(svgText).withContentType("image/svg+xml")
  } @@ cors(corsConfig)


  private val app = Http.collect[Request] {

    case req @ Method.GET -> !! / Routes.semanticdb =>
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

    case req @ Method.GET -> !! / Routes.classes =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(toTextDocuments)
        .map(InheritanceDiagram.fromTextDocuments)
        .map(_.toJson)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / Routes.source =>
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

  private def toTextDocuments(docs: TextDocumentsWithSourceSeq): TextDocuments =
    TextDocuments.apply(docs.documentsWithSource.flatMap(_.documents))

  private def readTextDocumentsWithSource(path: Option[List[String]]): Option[TextDocumentsWithSourceSeq] =
    for p <- combinePaths(path) yield
      TextDocumentsWithSourceSeq(
        All.scan(p).map: (path, d) =>
          TextDocumentsWithSource(path.toString).withDocuments(d.documents)
      )

  private def readSource(paths: Option[List[String]]): Option[String] =
    for
      path <- paths.toList.flatten.headOption
    yield
      Using.resource(scala.io.Source.fromFile(path)) { bufferedSource =>
        bufferedSource.getLines().mkString("\n")
      }

  private def combinePaths(path: Option[List[String]]): Option[file.Path] =
    for case h :: t <- path if h.nonEmpty yield
      file.Paths.get(h, t*)

  private def getPath(req: Request): Option[List[String]] =
    getParam(req, "path")

  private def getParam(req: Request, name: String) =
    req.url.queryParams.get(name)

  private def toTask[A](a: => A, error: String = "")(using ZIOConstructor[Nothing, Any, A], Trace) =
    ZIO.from(a).mapError(e => Throwable(if error.isEmpty then e.toString else error))

  private lazy val corsConfig =
    CorsConfig(allowedOrigins = _ => true)

  private lazy val badRequest =
    Response.status(Status.BadRequest)
