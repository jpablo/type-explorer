package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.StoredString
import org.scalajs.dom
import zio.json.*

def persistent[A: JsonCodec](storedString: StoredString, initial: A)(using
    Owner
): Var[A] =
  val aVar: Var[A] =
    Var {
      storedString.signal
        .map {
          _.fromJson[A].left
            .map(dom.console.error(_))
            .getOrElse(initial)
        }
        .observe
        .now()
    }
  aVar.signal.foreach: a =>
    storedString.set(a.toJson)
  aVar