package org.jpablo.typeexplorer.backend.textDocuments

import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import zio.*

import java.nio.file.Path

def readTextDocumentsWithSource(paths: List[Path]): Task[TextDocumentsWithSourceSeq] =
  ZIO
    .foreach(paths): basePath =>
      All.scan(basePath).map(docs => (basePath, docs))
    .map: combined =>
      TextDocumentsWithSourceSeq(
        combined.flatMap:
          case (basePath, docs) =>
            docs.map: (semanticDbUri, textDocuments) =>
              TextDocumentsWithSource(basePath.toString, semanticDbUri.toString, textDocuments.documents)
      )
