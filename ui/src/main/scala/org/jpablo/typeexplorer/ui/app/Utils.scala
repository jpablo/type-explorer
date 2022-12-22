package org.jpablo.typeexplorer.ui.app

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


extension [A] (set: Set[A])
  def toggle(a: A) =
    set.toggleWith(a, !set.contains(a))

  def toggleWith(a: A, b: Boolean) =
    if b then set + a else set - a


extension [K] (map: Map[K, Boolean])
  def toggle(k: K, initial: Boolean = false) =
    map + (k -> !map.getOrElse(k, initial))
