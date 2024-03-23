package org.jpablo.typeexplorer.backend.webApp

import org.jpablo.typeexplorer.backend.backends.plantuml.toSVGText
import org.jpablo.typeexplorer.backend.textDocuments.readTextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, toPlantUML}
import org.jpablo.typeexplorer.shared.webApp.{Endpoints, InheritanceRequest, port}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import zio.*
import zio.http.*
import zio.http.Header.AccessControlAllowOrigin
import zio.http.Middleware.{CorsConfig, cors}
import zio.json.*
import zio.stream.ZStream

import java.nio.file
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.util.matching.Regex

object WebApp extends ZIOAppDefault:
  // ----------
  // static files
  // ----------
  private val extension: Regex = """.*\.(css|js)$""".r
  // find the path of the current jar file
  private val jarPath = Paths.get(getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)

  private def resolveStaticPath(path: String): file.Path =
    val from = (_: file.Path).resolve("static").resolve(path)
    val p1 = from(jarPath)
    val p2 = from(jarPath.getParent.getParent)
    if Files.exists(p1) then p1 else p2

  given JsonCodec[file.Path] =
    JsonCodec.string.transform(file.Path.of(_), _.toString)

  private val index = Handler.fromFile(resolveStaticPath("index.html").toFile)

  private val staticRoutes =
    Routes(
      Method.GET / ""                    -> index.orDie,
      Method.GET / zio.http.uuid("uuid") -> index.orDie,
      Method.GET / "assets" / string("path") -> handler { (path: String, _: Request) =>
        val file = resolveStaticPath(s"assets/$path")
        val response = http.Response(status = Status.Ok, body = Body.fromStream(ZStream.fromPath(file)))
        path match
          case extension("css") => response.addHeader(Header.ContentType(MediaType.text.css))
          case extension("js")  => response.addHeader(Header.ContentType(MediaType.application.javascript))
          case _                => response
      }
    ).toHttpApp

  // ----------
  // endpoints
  // ----------
  private val apiRoutes =
    Routes(
      Method.POST / Endpoints.api / Endpoints.inheritance -> handler { (req: Request) =>
        for
          body <- req.body.asString
          ireq <- ZIO.from(body.fromJson[InheritanceRequest[file.Path]]).mapError(Throwable(_))
          docs <- readTextDocumentsWithSource(ireq.paths)
          symbols = ireq.activeSymbols.map(_._1).toSet
          diagram = InheritanceGraph.from(docs).subdiagram(symbols)
          puml = diagram.toPlantUML(ireq.activeSymbols.toMap, ireq.options, ireq.projectSettings)
          svgText <- puml.toSVGText("laminar")
        yield Response.text(svgText).addHeader(Header.ContentType(MediaType.image.`svg+xml`))
      }.orDie,
      //
      Method.GET / Endpoints.api / Endpoints.classes -> handler { (req: Request) =>
        toTaskOrBadRequest(getPath(req)): paths =>
          readTextDocumentsWithSource(paths)
            .map(InheritanceGraph.from)
            .map(_.toJson)
            .map(Response.json)
      }.orDie,
      //
      Method.GET / "semanticdb.json" -> handler { (req: Request) =>
        toTaskOrBadRequest(getPath(req)): paths =>
          readTextDocumentsWithSource(paths)
            .map(write)
            .map(Response.json)
      }.orDie,
      //
      Method.GET / Endpoints.api / Endpoints.source -> handler { (req: Request) =>
        val firstPath = getPath(req).flatMap(_.headOption)
        toTaskOrBadRequest(firstPath): path =>
          readSource(path).map(Response.text)
      }.orDie
    ).toHttpApp @@ cors(corsConfig)

  welcomeUser()

  override def run =
    Server.serve(staticRoutes ++ apiRoutes).provide(Server.defaultWithPort(port))

  def welcomeUser(): Unit =
    println("--------------------------------------------------")
    println("Welcome to Type Explorer!")
    println(org.jpablo.typeexplorer.BuildInfo.toString)
    println(s"Open your browser at http://localhost:$port/")
    println(s"Press Ctrl-C to stop the server")
    println("--------------------------------------------------")

  // -----------------
  // helper functions
  // -----------------

  given formats: Formats =
    Serialization.formats(NoTypeHints)

  private def readSource(path: file.Path): Task[String] = ZIO.scoped:
    ZIO
      .acquireRelease(ZIO.attemptBlocking(Source.fromFile(path.toFile)))(s => ZIO.succeedBlocking(s.close()))
      .map(_.getLines().mkString("\n"))

  private def getPath(req: Request): Option[List[file.Path]] =
    getParam(req, "path").map(_.map(file.Path.of(_)))

  private def getParam(req: Request, name: String): Option[List[String]] =
    req.url.queryParams.getAll(name).map(_.toList)

  private def toTaskOrBadRequest[A](oa: Option[A])(f: A => Task[Response]): Task[Response] =
    oa match
      case None    => ZIO.succeed(badRequest)
      case Some(a) => f(a)

  private lazy val corsConfig =
    CorsConfig(allowedOrigin = _ => Some(AccessControlAllowOrigin.All))

  private lazy val badRequest =
    Response.status(Status.BadRequest)
