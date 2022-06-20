package org.jpablo.typeexplorer.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.TextDocumentsWithSource

object InheritanceTree:

  def buildTree($documents: EventStream[List[TextDocumentsWithSource]]): EventStream[List[HtmlElement]] =
    for documentsWithSource <- $documents yield
      for
        documentWithSource <- documentsWithSource
        textDocument <- documentWithSource.documents
      yield
        div(
          textDocument.uri
        )

end InheritanceTree
