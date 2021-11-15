package semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.SymbolOccurrence.Role
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments}

object SymbolRegistry {

  /**
    * Scans the Path `p` for *.semanticdb files and creates a mapping of symbols to SymbolInformation
    * @param p Path to scan
    * @return
    */
  def scan(p: Path): List[(SymbolInformation, List[SymbolOccurrence])] =
    val documents = collection.mutable.Map.empty[SymbolInformation, List[SymbolOccurrence]].withDefaultValue(List.empty)
    semanticdb.Locator(p) { (_, payload: TextDocuments) =>
      for document: TextDocument <- payload.documents do
        val occurrences: Map[String, Seq[SymbolOccurrence]] = document.occurrences.groupBy(_.symbol).withDefaultValue(List.empty)
        for si <- document.symbols do
          documents += (si -> occurrences(si.symbol).toList)
    }
    documents.toList



  given rangeOrd: Ordering[semanticdb.Range] with
    def compare(t1: semanticdb.Range, t2: semanticdb.Range): Int =
      require(t1.startLine <= t1.endLine)
      require(t2.startLine <= t2.endLine)
      if t1.startLine == t1.endLine then require(t1.startCharacter <= t1.endCharacter)
      if t2.startLine == t2.endLine then require(t2.startCharacter <= t2.endCharacter)

      if t1 == t2 then 0
      else
        val o1 = Ordering.Int.compare(t1.startLine, t2.startLine)
        val o2 = Ordering.Int.compare(t1.startCharacter, t2.startCharacter)
        val o3 = Ordering.Int.compare(t1.endLine, t2.endLine)
        val o4 = Ordering.Int.compare(t1.endCharacter, t2.endCharacter)
        if o1 != 0 then o1
        else if o2 != 0 then o2
        else if o3 != 0 then o3
        else o4

  def callGraph(document: TextDocument) =
    var symbols = document.symbols.groupBy(_.symbol)
    var stack = List.empty[String]
    var references = List.empty[(String, String)]
    val sortedOccurrences = document.occurrences.sortBy(_.range)
    for occurrence <- sortedOccurrences do {
      symbols.get(occurrence.symbol) match {
        case Some(si) =>
          val notParamNorLocal = !List(Kind.PARAMETER, Kind.LOCAL).contains(si.head.kind)

          if occurrence.role == Role.DEFINITION && notParamNorLocal
          then // start a new scope
            val base = stack.dropWhile(h => !occurrence.symbol.contains(h))
            stack = occurrence.symbol :: base
            println("-----------");
            println(stack)
            println("-----------");
            println("\n")
          else
            if occurrence.role != Role.DEFINITION && notParamNorLocal
            then references :+= (stack.head -> occurrence.symbol)

          println(s"${occurrence.range.get}, ${occurrence.symbol}: ${occurrence.role}")
          println(s"\t\t\t$si")
        case _ =>
      }

    }
    println("---------- references ---------------")
    references.foreach(println)


}
