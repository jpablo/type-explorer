package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.Path

import scala.meta.internal.semanticdb.SymbolOccurrence
import scala.scalajs.js
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode2

def SourceCodeTab(sourceCode: EventStream[Option[Path]], position: Option[SymbolOccurrence] = None) =
  div(
    pre(
      child <--
        sourceCode.flatMap {
          case Some(value) =>
            fetchSourceCode2(value).map: t =>
              code(
                cls := "language-scala",
                t,
                onMountCallback { ctx =>
                  js.Dynamic.global.Prism.highlightElement(ctx.thisNode.ref)
                }
              )
          case None =>
            EventStream.fromValue(code())
        }
    )
  )
