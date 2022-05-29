package app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom.Document

def centerColumn(newDiagramStream: EventStream[Document]) =
  div (idAttr := "te-center-column", cls := "col-6",
    div (
      child.text <-- newDiagramStream.map(_.nodeValue)
    )
  )
