package semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments}

object SymbolRegistry {

  /**
    * Scans the Path `p` for *.semanticdb files and creates a mapping of symbols to SymbolInformation
    * @param p Path to scan
    * @return
    */
  def scan(p: Path) = {
    val documents = collection.mutable.Map.empty[SymbolInformation, List[SymbolOccurrence]].withDefaultValue(List.empty)
    semanticdb.Locator(p) { (_, payload: TextDocuments) =>
      for (document <- payload.documents) do
        val occurrences = document.occurrences.groupBy(_.symbol).withDefaultValue(List.empty)
        for si <- document.symbols do
          documents += (si -> occurrences(si.symbol).toList)
    }
    documents.toList
  }

}
