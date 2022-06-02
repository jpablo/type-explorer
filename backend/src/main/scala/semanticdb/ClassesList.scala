package semanticdb

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, TypeSignature, ValueSignature}
import java.nio.file.Paths
import java.net.URI
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import models.{Package, Type, Method}

object ClassesList {

  def scan (p: Path): List[Type] = {
    
    val documents = collection.mutable.ArrayBuffer.empty[SymbolInformation]

    semanticdb.Locator (p) { (_, payload: TextDocuments) =>
      for 
        document <- payload.documents
        sym <- document.symbols
        if sym.kind == Kind.OBJECT
      do
        documents += sym
    }

    documents.toList.map { (sym: SymbolInformation) =>
      val sl =
        for
          ne <- sym.signature.asNonEmpty.toList
          scope <- ne match
            case cs: ClassSignature => cs.declarations.toList
            case _ => List.empty
          symlink <- scope.symlinks
        yield
          symlink

//      val ss =
//        sym.signature.asNonEmpty.map { ne =>
//          ne match
//            case ValueSignature(tpe) => None
//            case ClassSignature(typeParameters, parents, self, declarations: Option[Scope]) =>
//              declarations match
//                case Some(scope) =>
//                  Some(scope.symlinks)
//                case None =>
//                  None
//            case MethodSignature(typeParameters, parameterLists, returnType) => None
//            case TypeSignature(typeParameters, lowerBound, upperBound) => None
//        }
      Type (
        sym.displayName,
        Some (Package (sym.symbol.split ("/").init.mkString ("/"))),
        sl.map { fullName =>
          val parts = fullName.split ("""\.""")
          val name = if parts.length > 1 then parts(1) else ""
          Method(name)
        }
      )
    }
  }

  
}


@main
def testScanClasses =
  val paths = Paths.get (new URI(
    "file:///Users/jpablo/proyectos/playground/type-explorer/.bloop/"
  ))
  val lst = ClassesList.scan (paths)
  lst.foreach (println)
