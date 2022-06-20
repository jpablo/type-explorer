package org.jpablo.typeexplorer.webApp

import org.jpablo.typeexplorer.backends.plantuml.PlantumlInheritance
import org.jpablo.typeexplorer.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.semanticdb.All

import java.net.URI
import java.nio.file
import org.jpablo.typeexplorer.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.json4s.*
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import scala.meta.internal.semanticdb.TextDocuments
import zhttp.http.*
import zhttp.http.Middleware.cors
import zhttp.http.middleware.Cors.CorsConfig
import zhttp.service.Server
import zio.*
import zio.json.*
import zio.prelude.AnySyntax


object WebApp extends ZIOAppDefault:

  // ----------
  // endpoints
  // ----------
  val app = Http.collect[Request] {

    case req @ Method.GET -> !! / "semanticdb" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(_.toByteArray)
        .map(Chunk.fromArray)
        .map(HttpData.fromChunk)
        .map(d => Response(data = d))
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

    case req @ Method.GET -> !! / "inheritance" =>
      (req |> getPath |> readTextDocumentsWithSource)
        .map(toTextDocuments)
        .map(InheritanceDiagram.fromTextDocuments)
        .map(PlantumlInheritance.fromInheritanceDiagram)
        .map(_.toSVG("laminar"))
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

  def readTextDocumentsWithSource(path: Option[List[String]]): Option[TextDocumentsWithSourceSeq] =
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

