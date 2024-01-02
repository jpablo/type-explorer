package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import scala.annotation.targetName

/*
  Input and Button have the same type erasure:
    `ReactiveHtmlElement[Any]`

  This has two consequences:
    1. Extensions with same name have to be disambiguated with @targetName
    2. They have to be defined in the same file.
 */

extension (elem: Input)
  @targetName("InputSmall")
  def small: Input = elem.amend(cls := "form-control-sm")

extension (btn: Button)
  def outline: Button = btn.amend(cls := "btn-outline")
  def primary: Button = btn.amend(cls := "btn-primary")
  def secondary: Button = btn.amend(cls := "btn-secondary")
  def success: Button = btn.amend(cls := "btn-success")
  @targetName("ButtonSmall")
  def small: Button = btn.amend(cls := "btn-sm")
  def tiny: Button = btn.amend(cls := "btn-xs")
  def circle: Button = btn.amend(cls := "btn-circle")
  def ghost: Button = btn.amend(cls := "btn-ghost")
