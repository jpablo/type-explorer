package app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom.{Element, Node}

def centerColumn(newDiagramStream: EventStream[Element]) =
  div (
    idAttr := "te-center-column",
    cls := "col-6",
    div (
      inContext { thisNode =>
        newDiagramStream --> { svg =>
          thisNode.ref.innerHTML = ""
          thisNode.ref.appendChild(svg)
          ()
        }
      }
    )
  )
