package org.jpablo.typeexplorer.ui.app

import scalajs.js
import scalajs.js.Dynamic.global

opaque type Path = String

object Path:
  def apply(value: String): Path = value
  def empty: Path = ""
  extension (s: Path) def toString: String = s


extension [A] (sa: Set[A])
  def toggle(a: A) = 
    if sa contains a then sa - a else sa + a


object console:
  def log(args: scala.scalajs.js.Any*) = global.console.log(args*)