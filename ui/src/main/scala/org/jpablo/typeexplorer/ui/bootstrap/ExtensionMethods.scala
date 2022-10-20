package org.jpablo.typeexplorer.ui.bootstrap

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
  @targetName("IinputSmall") 
  def small: Input = elem.amend(cls := "form-control-sm")  


extension (btn: Button)
  def outlineSecondary: Button = btn.amend(cls := "btn-outline-secondary")
  def outlineSuccess  : Button = btn.amend(cls := "btn-outline-success")
  def small           : Button = btn.amend(cls := "btn-sm")    
