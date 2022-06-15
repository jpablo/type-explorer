package widgets

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.airstream.core.Signal
import scalajs.js.URIUtils.encodeURIComponent
import org.scalajs.dom

//def collapsable2(head: HtmlElement, $body: Signal[Seq[HtmlElement]], open: Boolean = false) =
//  val $open = Var(open)
//  val $managedChildren =
//    $open.signal.combineWith($body).mapN((o, children) => if o then children else Seq.empty)
//
//  li(
//    cls := "collapsable-wrapper",
//    a(
//      cls := "bi",
//      cls <-- $open.signal.map(o => if o then "bi-chevron-down" else "bi-chevron-right"),
//      onClick --> $open.updater((v, _) => !v)
//    ),
//    head,
//    ul( children <-- $managedChildren )
//  )

def collapsable(head: HtmlElement, $children: Signal[Seq[HtmlElement]], open: Boolean = false) =
  val $open = Var(open)
  val $managedChildren =
    $open.signal
      .combineWith($children)
      .mapN((open, children) =>
        if open then
          children.map(li(_))
        else Seq.empty
      )

  div(
    cls := "collapsable-wrapper",
    Icons.chevron(
      $open.signal,
      cls := "collapsable-button",
      onClick --> $open.updater((v, _) => !v)
    ),
    head,
    ul(
      cls := "collapsable-children",
      children <-- $managedChildren
    )
  )


object Icons:

  def chevron(
    $open: Signal[Boolean],
    mods: Modifier[ReactiveHtmlElement[dom.html.Anchor]]*
  ) =
    a(
      cls := "bi",
      cls <-- $open.map(o => if o then "bi-chevron-down" else "bi-chevron-right")
    ).amend(mods)

  def fileBinary =
    i(cls := "bi bi-file-binary")
