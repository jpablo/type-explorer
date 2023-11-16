package org.jpablo.typeexplorer.ui.domUtils

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.{BooleanAsAttrPresenceCodec, IntAsStringCodec, StringAsIsCodec}
import org.scalajs.dom.HTMLDialogElement

val details = htmlTag("details")
val summary = htmlTag("summary")
val dialog = htmlTag[HTMLDialogElement]("dialog")

val open = htmlAttr("open", BooleanAsAttrPresenceCodec)
val dataTip = htmlAttr("data-tip", StringAsIsCodec)

val autocomplete = htmlProp("autocomplete", StringAsIsCodec)
val min = htmlAttr("min", IntAsStringCodec)
val max = htmlAttr("max", IntAsStringCodec)
