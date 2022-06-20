package org.jpablo.typeexplorer.semanticdb

import org.jpablo.typeexplorer.models.{Method, Namespace, Package}
import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, TypeSignature, ValueSignature}
import java.nio.file.Paths
import java.net.URI
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.collection.mutable


object All:
  def scan(p: Path): List[(Path, TextDocuments)] =
    val documents = mutable.ArrayBuffer.empty[(Path, TextDocuments)]
    semanticdb.Locator(p) { (path, textDocuments) =>
      documents += ((path, textDocuments))
    }
    documents.toList
