package org.jpablo.typeexplorer.backend.semanticdb

import zio.*

import java.nio.file.Path
import scala.collection.mutable
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.TextDocuments

object All:
  def scan(p: Path): Task[List[(Path, TextDocuments)]] =
    ZIO.attempt:
      val documents = mutable.ArrayBuffer.empty[(Path, TextDocuments)]
      semanticdb.Locator(p): (path, textDocuments) =>
        documents += ((path, textDocuments))
      documents.toList
