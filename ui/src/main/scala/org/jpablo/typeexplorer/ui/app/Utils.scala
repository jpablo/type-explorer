package org.jpablo.typeexplorer.ui.app

import scalajs.js
import scalajs.js.Dynamic.global
import zio.json.*

opaque type Path = String

object Path:
  def apply(value: String): Path = value
  def empty: Path = ""
  extension (s: Path) def toString: String = s
  given JsonCodec[Path] = JsonCodec.string
  given JsonFieldEncoder[Path] = JsonFieldEncoder.string
  given JsonFieldDecoder[Path] = JsonFieldDecoder.string


extension [A] (sa: Set[A])
  def toggle(a: A) =
    if sa contains a then sa - a else sa + a


