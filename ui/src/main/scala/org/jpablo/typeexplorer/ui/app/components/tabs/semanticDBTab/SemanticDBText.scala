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
      h4(cls := "p-2", textDoc.semanticDbUri),
      div(
        textDoc.documents.map(renderTextDocument)
      )
    )

  private def renderTextDocument(doc: TextDocument) =
    div(
      idAttr := doc.uri,
      textCard("", "card-uri", s"uri: ${doc.uri}"),
      div(
        doc.symbols
          .sortBy(_.symbol)
          .map(si => renderGeneratedMessage(encodeURIComponent(si.symbol), "card-symbol-information", si))
      ),
      div(
        doc.occurrences.map(oc => renderGeneratedMessage(encodeURIComponent(oc.symbol), "card-occurrence", oc))
      ),
      div(
        doc.synthetics.map(syn =>
          renderGeneratedMessage(syn.range.map(_.toProtoString).getOrElse(""), "card-synthetic", syn)
        )
      )
    )

  private def renderGeneratedMessage(id: String, className: String, msg: GeneratedMessage) =
    textCard(id, className, msg.toProtoString)

  private def textCard(id: String, className: String, text: String) =
    div(
      idAttr := id,
      cls    := ("m-1", className),
      div(
        cls := "p-2",
        pre(
          cls := "m-0 text-xs",
          code(
            cls := "language-protobuf 0 text-xs",
            onMountCallback: ctx =>
              js.Dynamic.global.Prism.highlightElement(ctx.thisNode.ref),
            text
          )
        )
      )
    )

end SemanticDBText
