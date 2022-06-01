package semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments}
import java.nio.file.Paths
import java.net.URI

object ClassesList {

  def scan(p: Path) = {
    
    val documents = collection.mutable.ArrayBuffer.empty[String]

    semanticdb.Locator(p) { (_, payload: TextDocuments) =>
      for (document <- payload.documents) do
          documents += document.uri
    }
    documents.toList
  }

  
}


@main
def testScanClasses =
  val paths = Paths.get(new URI(
    "file:///Users/jpablo/proyectos/playground/type-explorer/.bloop/"
  ))
  val lst = ClassesList.scan(paths)
  lst.take(1).foreach(println)
