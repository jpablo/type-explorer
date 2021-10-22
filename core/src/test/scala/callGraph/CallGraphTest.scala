package callGraph

import semanticdb.SymbolRegistry
import zio.test.*
import zio.test.Assertion.*

import java.net.URI
import java.nio.file.{Files, Paths}
// import zio.test.magnolia.DeriveGen

import scala.meta.*

object CallGraphTest extends DefaultRunnableSpec {

  val path = Paths.get(new URI("file:///Users/jpablo/proyectos/varios/type-explorer-core/core/src/test/resources/QueryController.scala.semanticdb"))
//  val path = Paths.get(new URI("file:///Users/jpablo/code/event-horizon/event-horizon-api/app/controllers/QueryController.scala"))
//  val bytes = Files.readAllBytes(path)
//  val text = new String(bytes, "UTF-8")
//  val input = Input.VirtualFile(path.toString, text)
//  val exampleTree = input.parse[Source].get

  val symbols = SymbolRegistry.scan(path)

  def spec = suite("suite")(
    test("test") {

      // IDEA: construct a btree with the line/char positions of each definition
      // so that each reference can be located
      symbols.foreach(println)

      assert(1)(equalTo(1))
    }
  )
}