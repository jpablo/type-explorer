package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.{Namespace, SymbolRange}
import org.jpablo.typeexplorer.ui.app.Path

import scala.meta.internal.semanticdb.SymbolOccurrence
import scala.scalajs.js
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode2

def SourceCodeTab(sourceCode: EventStream[Option[(Namespace, Path)]], position: Option[SymbolOccurrence] = None) =
  div(
      child <--
        sourceCode.flatMap {
          case Some((ns, path)) =>
            fetchSourceCode2(path).map: text =>
              println(ns.range)
              val r = ns.range.map(r => s"${r.startLine}-${r.endLine}").getOrElse("0")
              pre(cls := "line-numbers", dataAttr("line") := r,
                code(cls := "language-scala",
                  text,
                  onMountCallback { ctx =>
                    js.Dynamic.global.Prism.highlightElement(ctx.thisNode.ref)
                  }
                )
            )
          case None =>
            EventStream.fromValue(pre())
        }
  )
