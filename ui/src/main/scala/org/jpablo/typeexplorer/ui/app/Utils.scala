package org.jpablo.typeexplorer.ui.app




opaque type Path = String

object Path:
  def apply(value: String): Path = value
  def empty: Path = ""

  extension (s: Path)
    def toString: String = s

