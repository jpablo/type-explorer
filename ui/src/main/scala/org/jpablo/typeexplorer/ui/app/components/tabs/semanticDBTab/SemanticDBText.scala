package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import scalapb.GeneratedMessage
import scalajs.js
import scalajs.js.URIUtils.encodeURIComponent
import scala.meta.internal.semanticdb.TextDocument


object SemanticDBText:

  def apply(textDoc: TextDocumentsWithSource) =
    div(
      idAttr := textDoc.semanticDbUri,
      cls := "semanticdb-document",
      h4(cls := "semanticdb-document-file", textDoc.semanticDbUri),
      div(
        cls := "text-document-container",
        textDoc.documents.map(renderTextDocument)
      )
    )

  private def renderTextDocument(doc: TextDocument) =
    div(
      idAttr := doc.uri,
      cls := "text-document",
      textCard("", "card-uri",  s"uri: ${doc.uri}"),
      div(
        cls := "symbol-information-container",
        doc.symbols.sortBy(_.symbol).map(si => renderGeneratedMessage(encodeURIComponent(si.symbol), "card-symbol-information", si))
      ),
      div(
        cls := "occurrences-container",
        doc.occurrences.map(oc => renderGeneratedMessage(encodeURIComponent(oc.symbol), "card-occurrence", oc)),
      ),
      div(
        cls := "synthetics-container",
        doc.synthetics.map(syn => renderGeneratedMessage(syn.range.map(_.toProtoString).getOrElse(""), "card-synthetic", syn)),
      )
    )

  private def renderGeneratedMessage(id: String, className: String, msg: GeneratedMessage) =
    textCard(id, className, msg.toProtoString)

  private def textCard(id: String, className: String, text: String) =
    div(
      idAttr := id,
      cls := ("card", className),
      div(
        cls := "card-body",
        pre(
          code(
            cls := "language-protobuf",
            onMountCallback { ctx => 
              js.Dynamic.global.Prism.highlightElement(ctx.thisNode.ref)
            },
            text
          )
        )
      )
    )

end SemanticDBText
