package org.jpablo.typeexplorer.backend.textDocuments

import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import java.nio.file
import scala.meta.internal.semanticdb.TextDocuments

def readTextDocumentsWithSource(basePaths: List[file.Path]) =
  TextDocumentsWithSourceSeq {
    for
      basePath <- basePaths
      (semanticDbUri, textDocuments) <- All.scan(basePath)
    yield TextDocumentsWithSource(
      basePath = basePath.toString,
      semanticDbUri = semanticDbUri.toString,
      documents = textDocuments.documents
    )
  }
