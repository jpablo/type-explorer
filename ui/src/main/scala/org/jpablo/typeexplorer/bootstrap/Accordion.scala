package org.jpablo.typeexplorer.bootstrap

import com.raquo.laminar.api.L.*

object Accordion {

  val `accordion-flush` = "accordion-flush"
  val open = "open"

  def accordion[S](
    section         : EventStream[List[S]],
    sectionId       : S => String,
    sectionHeader   : S => String,
    sectionChildren : S => List[Div],
    alwaysOpen      : Boolean = false
  ): Div =
    div(
      cls := "accordion",
      children <--
        section.split(sectionId)(renderSection(sectionChildren, sectionHeader, alwaysOpen))
    )


  private def renderSection[S](
    buildElements: S => List[Div],
    sectionHeader: S => String,
    alwaysOpen   : Boolean = false
  )(
    id: String,
    initial: S,
    sectionElement: EventStream[S]
  ): Div =
    div(cls := "accordion-item",
      div(cls := "accordion-header",
        button(
          cls := "accordion-button",
          typ := "button",
          dataAttr("bs-toggle") := "collapse",
          dataAttr("bs-target") := "#" + id,
          child.text <-- sectionElement.map(sectionHeader)
        )
      ),
      div(cls := "accordion-collapse collapse show",
        if alwaysOpen then emptyMod else idAttr := id,
        div(
          cls := "accordion-body",
          children <-- sectionElement.map(buildElements)
        )
      )
    )
}

