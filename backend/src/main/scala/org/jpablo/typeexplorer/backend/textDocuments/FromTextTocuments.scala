package org.jpablo.typeexplorer.backend.textDocuments

import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}

import java.nio.file
import scala.meta.internal.semanticdb.TextDocuments
import zio.*

import java.nio.file.Path

def readTextDocumentsWithSource(paths: List[file.Path]): Task[TextDocumentsWithSourceSeq] =
  ZIO
    .foreach(paths): path =>
      All.scan(path).map(docs => (path, docs))
    .map: combined =>
      TextDocumentsWithSourceSeq(
        combined.flatMap:
          case (path, docs) =>
            docs.map: (semanticDbUri, textDocuments) =>
              TextDocumentsWithSource(path.toString, semanticDbUri.toString, textDocuments.documents)
      )
