package webApp

import zio.*
import zhttp.http.*
import zhttp.service.Server

object WebApp extends ZIOAppDefault {

  val app = Http.collect[Request] {
    case Method.GET -> !! / "text" => Response.text("Hello World!")
  }

  val run =
    Server.start(8090, app)
}
