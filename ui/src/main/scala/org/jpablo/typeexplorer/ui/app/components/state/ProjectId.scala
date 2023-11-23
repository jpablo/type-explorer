package org.jpablo.typeexplorer.ui.app.components.state

import zio.json.*
import scala.scalajs.js

case class ProjectId(value: String) extends AnyVal

object ProjectId:
  given JsonCodec[ProjectId] =
    JsonCodec(
      JsonEncoder.string.contramap(_.value),
      JsonDecoder.string.map(ProjectId(_))
    )
  given JsonFieldEncoder[ProjectId] =
    JsonFieldEncoder.string.contramap(_.value)
  given JsonFieldDecoder[ProjectId] =
    JsonFieldDecoder.string.map(ProjectId(_))

  def random =
    ProjectId(js.Dynamic.global.crypto.randomUUID().toString)

