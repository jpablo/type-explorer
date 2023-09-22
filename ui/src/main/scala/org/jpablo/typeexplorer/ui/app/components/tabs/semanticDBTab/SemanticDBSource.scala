package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import scala.meta.internal.semanticdb.SymbolOccurrence
import scalajs.js
import com.raquo.airstream.core.EventStream

def SourceCodeTab($sourceCode: EventStream[String], position: Option[SymbolOccurrence] = None) =
  div(
    pre(
      child <-- $sourceCode.map { txt => 
        code(
          cls := "language-scala",
          txt,
          onMountCallback { ctx => 
            js.Dynamic.global.Prism.highlightElement(ctx.thisNode.ref)
          }
        )
      }
    )
  )
