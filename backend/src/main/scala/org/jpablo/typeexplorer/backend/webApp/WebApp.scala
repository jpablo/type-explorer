package org.jpablo.typeexplorer.backend.webApp

import org.jpablo.typeexplorer.backend.backends.plantuml.toSVG
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples, PlantUML, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.{InheritanceRequest, Routes}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import zhttp.http.*
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import zio.*
import zio.ZIO.ZIOConstructor
import zio.json.*
import zio.prelude.AnySyntax
import zio.stream.ZStream

import java.io.File
import java.net.URI
import java.nio.file
import java.nio.file.Paths
import scala.meta.internal.semanticdb.TextDocuments
import scala.util.Using
import scala.util.matching.Regex

object WebApp extends ZIOAppDefault:
  val extension: Regex = """.*\.(css|js)$""".r
  // find the path of the current jar file
  val jarPath = Paths.get(getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)
  val static = jarPath.getParent.getParent.resolve("static")

  private val staticRoutes = Http.collectHttp[Request] {
    case Method.GET -> !! =>
      Http.fromFile(static.resolve("index.html").toFile)

    case Method.GET -> !! / "assets" / path =>
      val file = static.resolve(s"assets/$path").toFile
      val response = Http.fromStream(ZStream.fromFile(file))

      path match
        case extension("css") => response.withContentType("text/css")
        case extension("js")  => response.withContentType("application/javascript")
        case _ => response

  }

  // ----------
  // endpoints
  // ----------
  private val appZ = Http.collectZIO[Request] {
    case req @ Method.POST -> !! / Routes.inheritanceDiagram =>
      def createDiagram(ireq: InheritanceRequest): Task[PlantUML] =
        for
          docs <- toTask(readTextDocumentsWithSource(ireq.paths), error = "No path provided")
          diagram = InheritanceDiagram.fromTextDocumentsWithSource(docs)
          puml =
            PlantumlInheritance.fromInheritanceDiagram(
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
      getPath(req)
        .map(readTextDocumentsWithSource)
        .map(_.toByteArray)
        .map(arr => Response(body = Body.fromChunk(Chunk.fromArray(arr))))
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.json" =>
      getPath(req)
        .map(readTextDocumentsWithSource)
        .map(write)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / "semanticdb.textproto" =>
      getPath(req)
        .map(readTextDocumentsWithSource)
        .map(_.toProtoString)
        .map(Response.text)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / Routes.classes =>
      getPath(req)
        .map(readTextDocumentsWithSource)
        .map(InheritanceDiagram.fromTextDocumentsWithSource)
        .map(_.toJson)
        .map(Response.json)
        .getOrElse(badRequest)

    case req @ Method.GET -> !! / Routes.source =>
      getPath(req)
        .flatMap(_.headOption)
        .map(readSource)
        .map(Response.text)
        .map(_.withContentType("text/plain"))
        .getOrElse(badRequest)

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, staticRoutes ++ appZ ++ app)

  // -----------------
  // helper functions
  // -----------------

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  private def readTextDocumentsWithSource(basePaths: List[String]): TextDocumentsWithSourceSeq =
    TextDocumentsWithSourceSeq {
      for
        basePath <- basePaths.map(file.Paths.get(_))
        (semanticDbUri, textDocuments) <- All.scan(basePath)
      yield
        TextDocumentsWithSource(
          basePath = basePath.toString,
          semanticDbUri = semanticDbUri.toString,
          documents = textDocuments.documents
        )
    }

  private def readSource(path: String): String =
    Using.resource(scala.io.Source.fromFile(path)) { bufferedSource =>
      bufferedSource.getLines().mkString("\n")
    }

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
