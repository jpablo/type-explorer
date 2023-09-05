package org.jpablo.typeexplorer.backend.webApp

import org.jpablo.typeexplorer.backend.backends.plantuml.toSVG
import org.jpablo.typeexplorer.backend.textDocuments.readTextDocumentsWithSource
import org.jpablo.typeexplorer.protos.TextDocumentsWithSourceSeq
import org.jpablo.typeexplorer.shared.inheritance.{
  InheritanceDiagram,
  toPlantUML
}
import org.jpablo.typeexplorer.shared.webApp.{InheritanceRequest, Routes}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import zhttp.http.*
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import zio.*
import zio.ZIO.ZIOConstructor
import zio.json.*
import zio.stream.ZStream

import java.nio.file
import java.nio.file.Paths
import scala.io.Source
import scala.util.matching.Regex

object WebApp extends ZIOAppDefault:
  // ----------
  // static files
  // ----------

  val extension: Regex = """.*\.(css|js)$""".r
  // find the path of the current jar file
  val jarPath = Paths.get(getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)
  val staticPath = jarPath.getParent.getParent.resolve("static")

  given JsonCodec[file.Path] =
    JsonCodec.string.transform(file.Path.of(_), _.toString)

  private val staticRoutes = Http.collectHttp[Request] {
    case Method.GET -> !! =>
      Http.fromFile(staticPath.resolve("index.html").toFile)

    case Method.GET -> !! / "assets" / path =>
      val file = staticPath.resolve(s"assets/$path").toFile
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
      for
        body <- req.body.asString
        ireq <- ZIO.from(body.fromJson[InheritanceRequest[file.Path]]).mapError(Throwable(_))
        docs <- readTextDocumentsWithSource(ireq.paths)
        symbols = ireq.activeSymbols.map(_._1).toSet
        diagram = InheritanceDiagram.from(docs).subdiagram(symbols)
        puml = diagram.toPlantUML(ireq.activeSymbols.toMap, ireq.options)
        svgText <- puml.toSVG("laminar")
      yield Response.text(svgText).withContentType("image/svg+xml")

    case req @ Method.GET -> !! / Routes.semanticdb =>
      toTaskOrBadRequest(getPath(req)): paths =>
        readTextDocumentsWithSource(paths)
          .map(_.toByteArray)
          .map(Body.fromChunk compose Chunk.fromArray)
          .map: ch =>
            Response(body = ch)
              .withContentType(HeaderValues.applicationOctetStream)

    case req @ Method.GET -> !! / "semanticdb.json" =>
      toTaskOrBadRequest(getPath(req)): paths =>
        readTextDocumentsWithSource(paths)
          .map(write)
          .map(Response.json)

    case req @ Method.GET -> !! / "semanticdb.textproto" =>
      toTaskOrBadRequest(getPath(req)): paths =>
        readTextDocumentsWithSource(paths)
          .map(_.toProtoString)
          .map(Response.text)

    case req @ Method.GET -> !! / Routes.classes =>
      toTaskOrBadRequest(getPath(req)): paths =>
        readTextDocumentsWithSource(paths)
          .map(InheritanceDiagram.from)
          .map(_.toJson)
          .map(Response.json)

    case req @ Method.GET -> !! / Routes.source =>
      val firstPath = getPath(req).flatMap(_.headOption)
      toTaskOrBadRequest(firstPath): path =>
        readSource(path).map(Response.text)

  } @@ cors(corsConfig)

  val run =
    Server.start(8090, staticRoutes ++ appZ)

  // -----------------
  // helper functions
  // -----------------

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  private def readSource(path: file.Path): Task[String] = ZIO.scoped:
    ZIO
      .acquireRelease(ZIO.attemptBlocking(Source.fromFile(path.toFile)))(s =>
        ZIO.succeedBlocking(s.close())
      )
      .map(_.getLines().mkString("\n"))

  private def getPath(req: Request): Option[List[file.Path]] =
    getParam(req, "path").map(_.map(file.Path.of(_)))

  private def getParam(req: Request, name: String): Option[List[String]] =
    req.url.queryParams.get(name)

  private def toTaskOrBadRequest[A](oa: Option[A])(f: A => Task[Response]): Task[Response] =
    oa match
      case None    => ZIO.succeed(badRequest)
      case Some(a) => f(a)

  private lazy val corsConfig =
    CorsConfig(allowedOrigins = _ => true)

  private lazy val badRequest =
    Response.status(Status.BadRequest)
