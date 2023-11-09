package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*

opaque type Path = String

object Path:
  def apply(value: String): Path = value
  def empty: Path = ""
  extension (s: Path)
    def toString: String = s
    def isEmpty: Boolean = s.isEmpty
  given JsonCodec[Path] = JsonCodec.string
  given JsonFieldEncoder[Path] = JsonFieldEncoder.string
  given JsonFieldDecoder[Path] = JsonFieldDecoder.string



