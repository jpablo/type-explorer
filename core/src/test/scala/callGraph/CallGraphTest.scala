package callGraph

import semanticdb.SymbolRegistry
import semanticdb.SymbolRegistry.given
import zio.test.*
import zio.test.Assertion.*

import java.net.URI
import java.nio.file.{Files, Paths}
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments}
// import zio.test.magnolia.DeriveGen

import scala.meta.*

object CallGraphTest extends DefaultRunnableSpec {

  val path = Paths.get(new URI("file:///Users/jpablo/proyectos/varios/type-explorer-core/core/src/test/resources/MetricsReporter.scala.semanticdb"))
//  val path = Paths.get(new URI("file:///Users/jpablo/code/event-horizon/event-horizon-api/app/controllers/QueryController.scala"))
//  val bytes = Files.readAllBytes(path)
//  val text = new String(bytes, "UTF-8")
//  val input = Input.VirtualFile(path.toString, text)
//  val exampleTree = input.parse[Source].get

  val symbols: List[(SymbolInformation, List[SymbolOccurrence])] =
    SymbolRegistry.scan(path)

  def spec = suite("suite")(
    test("test") {

      // IDEA: construct a btree with the line/char positions of each definition
      // so that each reference can be located
//      SymbolRegistry.callGraph(symbols)

      semanticdb.Locator(path) { (_, payload: TextDocuments) =>
        for doc <- payload.documents do
          SymbolRegistry.callGraph(doc)
      }

      assert(1)(equalTo(1))
    },

    test("Range Ordering") {
      val r1 = semanticdb.Range(0, 10, 1, 5)
      val r2 = semanticdb.Range(1, 4, 1, 10)

      assert(rangeOrd.compare(r1, r1))(equalTo(0))
      assert(rangeOrd.compare(r1, r2))(equalTo(-1))
      assert(rangeOrd.compare(r2, r1))(equalTo(1))

      assert(rangeOrd.compare(semanticdb.Range(1, 10, 1, 15), semanticdb.Range(0, 10, 1, 5)))(equalTo(1))
      assert(rangeOrd.compare(semanticdb.Range(1, 10, 1, 15), semanticdb.Range(1, 10, 1, 14)))(equalTo(1))
    }

  )
}