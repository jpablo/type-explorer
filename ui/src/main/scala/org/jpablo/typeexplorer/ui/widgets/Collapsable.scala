package org.jpablo.typeexplorer.ui.widgets

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.extensions.*

object Collapsable:

  class Control private (val $isOpen: Signal[Boolean], val toggle: Observer[Boolean])

  // This is Sub-Var or Var-projection of sorts.
  // The method Var#zoom does not provide an easy way to update the value of the Var
  // without an Owner.
  object Control:
    def apply(startOpen: Boolean, $openState: Var[Map[String, Boolean]])(key: String) =
      new Control(
        $openState.signal.map(_.getOrElse(key, startOpen)),
        Observer[Boolean](_ => $openState.update(_.toggle(key, startOpen)))
      )

  def apply(nodeLabel: HtmlElement, nodeContents: Seq[HtmlElement], control: Control) =
    div(
      cls := "collapsable-wrapper whitespace-nowrap cursor-pointer te-package-name",
      if nodeContents.isEmpty then span(cls := "bi inline-block w-5")
      else Icons.chevron(control.$isOpen, onClick.mapToTrue --> control.toggle),
      nodeLabel,
      control.$isOpen.childWhenTrue(
        ul(cls := "collapsable-children pl-4", nodeContents.map(li(_)))
      )
    )
