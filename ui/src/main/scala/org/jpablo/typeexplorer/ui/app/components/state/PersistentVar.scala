package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.StoredString
import org.scalajs.dom
import zio.json.*

/** A marker class to indicate that changes to the contents are persisted.
  */
case class PersistentVar[A](v: Var[A]):
  export v.zoom
  val signal = v.signal
  val update = v.update
  val updater = v.updater

def persistentVar[A: JsonCodec](storedString: StoredString, initial: A)(using
    Owner
): PersistentVar[A] =
  val str = storedString.signal.observe.now()
  val aVar =
    Var(str.fromJson[A].left.map(dom.console.error(_)).getOrElse(initial))
  aVar.signal.foreach: a =>
    storedString.set(a.toJson)
  PersistentVar(aVar)
