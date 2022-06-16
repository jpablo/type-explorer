package widgets

import com.raquo.laminar.api.L.*
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.airstream.core.Signal
import scalajs.js.URIUtils.encodeURIComponent
import org.scalajs.dom

def collapsable(branchLabel: HtmlElement, $children: Signal[Seq[HtmlElement]], open: Boolean = false) =
  val $open = Var(open)
  val $managedChildren =
    $open.signal
      .combineWith($children)
      .mapN((open, children) =>
        if open then children.map(li(_)) else Seq.empty
      )

  div(
    cls := "collapsable-wrapper",
    Icons.chevron(
      $open.signal,
      cls := "collapsable-button",
      onClick --> $open.updater((v, _) => !v)
    ),
    branchLabel,
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

  def fileCode =
    i(cls := "bi bi-file-code")

