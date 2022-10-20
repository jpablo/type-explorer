package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement


def Checkbox(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "checkbox", cls := "btn-check", mods)

def Label(forIdAttr: String, mods: Modifier[ReactiveHtmlElement.Base]*): Label =
  label(cls := "btn btn-outline-primary", forId := forIdAttr, mods)


def Search(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "search", cls := "form-control", mods)


// Note: extension method Search.small is defined together with Button.small for Scala technical reasons 
// (in file ExtensionMethods.scala)
