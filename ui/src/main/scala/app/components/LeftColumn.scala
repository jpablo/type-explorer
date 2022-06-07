package app.components

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream

def leftColumn($classes: EventStream[List[Type]]) =
  accordion (
    section         = $classes,
    sectionId       = _.name,
    sectionHeader   = _.name,
    sectionChildren = _.methods.map (m => div (m.name)),
    alwaysOpen      = true
  ) amend (
    idAttr := "te-left-column",
    cls := ("col", `accordion-flush`, open)
  )

