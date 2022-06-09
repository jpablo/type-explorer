package semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, TypeSignature, ValueSignature}
import java.nio.file.Paths
import java.net.URI
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import models.{Package, Type, Method}

object ClassesList:

  def scan(p: Path): List[Type] =
    val kinds = Set(Kind.OBJECT, Kind.CLASS, Kind.TRAIT)
    val symbols =
      collection.mutable.ArrayBuffer.empty[SymbolInformation]
    // --- load all documents ----
    semanticdb.Locator(p) { (_, textDocuments: TextDocuments) =>
      for
        document <- textDocuments.documents
        symbol <- document.symbols
        k: Kind = symbol.kind
        if kinds contains ??? //symbol.kind
      do
        symbols += symbol
    }
    // --------------------------

    symbols.toList.map { (symbol: SymbolInformation) =>
      val symlinks =
        for
          sig <- symbol.signature.asNonEmpty.toList
          scope <- sig match
            case cs: ClassSignature => cs.declarations.toList
            case _ => List.empty
          symlink <- scope.symlinks
        yield
          symlink

      val methods =
        symlinks.map { fullName =>
          val parts = fullName.split("""\.""")
          val name = if parts.length > 1 then parts(1) else ""
          Method(name)
        }

      Type(
        name = symbol.displayName,
        `package` = Some(Package(symbol.symbol.split("/").init.mkString("/"))),
        methods = methods
      )
    }
  end scan

end ClassesList


@main
def testScanClasses =
  val paths = Paths.get (new URI(
    "file:///Users/jpablo/proyectos/playground/type-explorer/.bloop/"
  ))
  val lst = ClassesList.scan (paths)
  lst.foreach (println)
