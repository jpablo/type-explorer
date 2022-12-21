package org.jpablo.typeexplorer.ui.widgets

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.laminext.syntax.core.*
import org.scalajs.dom
import org.scalajs.dom.html.LI

import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, Synthetic, TextDocument}
import scalajs.js.URIUtils.encodeURIComponent

def Collapsable(branchLabel: HtmlElement, contents: Seq[HtmlElement], open: Boolean = false) =
  val $open = Var(open)
  div(
    cls := "collapsable-wrapper whitespace-nowrap bg-slate-100 cursor-pointer te-package-name",
    if contents.isEmpty then
      span(cls := "bi inline-block w-5")
    else
      Icons.chevron($open.signal, onClick --> $open.updater((v, _) => !v)),
    branchLabel,
    $open.signal.childWhenTrue(
      ul(cls := "collapsable-children pl-4", contents.map(li(_)))
    )
  )

object Icons:

  def chevron(
    $open: Signal[Boolean],
    mods: Modifier[Anchor]*
  ) =
    a(
      cls := "bi inline-block w-5",
      cls <-- $open.map(o => if o then "bi-chevron-down" else "bi-chevron-right")
    ).amend(mods)

  def fileBinary =
    i(cls := "bi bi-file-binary")

  def fileCode =
    i(cls := "bi bi-file-code")

