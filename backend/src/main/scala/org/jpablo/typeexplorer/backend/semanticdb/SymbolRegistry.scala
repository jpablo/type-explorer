package org.jpablo.typeexplorer.backend.semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments}
import java.nio.file.Paths
import java.net.URI
import scala.collection.mutable

object SymbolRegistry {

  /**
    * Scans the Path `p` for *.semanticdb files and creates a mapping of symbols to SymbolInformation
    * @param p Path to scan
    * @return
    */
  def scan(p: Path): List[(SymbolInformation, List[SymbolOccurrence])] =
    val documents =
      mutable.Map.empty[SymbolInformation, List[SymbolOccurrence]].withDefaultValue(List.empty)

    semanticdb.Locator(p): (_, payload: TextDocuments) =>
      for (document <- payload.documents) do
        val occurrences = document.occurrences.groupBy(_.symbol).withDefaultValue(List.empty)
        for si <- document.symbols do
          documents += (si -> occurrences(si.symbol).toList)

    documents.toList
}


@main
def testScan =
  val paths = Paths.get(new URI(
    // "file:///Users/jpablo/proyectos/playground/type-explorer/.bloop/sharedJVM/bloop-bsp-clients-classes/classes-Metals-tWte2ISDQFO3HZuNZmpIQA==/META-INF/semanticdb/shared/src/main/scala/models/Models.scala.semanticdb",
    "file:///Users/jpablo/proyectos/playground/type-explorer/.bloop/"
  ))
  val lst = SymbolRegistry.scan(paths)
  lst.map(_._1.displayName).foreach(println)
