package org.jpablo.typeexplorer.backend.backends.graphviz

import zio.test.*
import zio.test.Assertion.*

import java.net.URI
import java.nio.file.Paths
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.TextDocuments
// import zio.test.magnolia.DeriveGen

import scala.meta.*


object CallGraphTest extends ZIOSpecDefault {

//  val path = Paths.get(new URI("file:///Users/jpablo/proyectos/varios/type-explorer-core/core/src/test/resources/QueryController.scala.semanticdb"))
  val paths = Paths.get(new URI("file:///Users/jpablo/code/Event-Horizon-API/target/scala-2.12/classes/META-INF/semanticdb/app/controllers/QueryController.scala.semanticdb"))
//  val path = Paths.get(new URI("file:///Users/jpablo/code/event-horizon/event-horizon-api/app/controllers/QueryController.scala"))
//  val bytes = Files.readAllBytes(path)
//  val text = new String(bytes, "UTF-8")
//  val input = Input.VirtualFile(path.toString, text)
//  val exampleTree = input.parse[Source].get

//  val symbols = SymbolRegistry.scan(paths)

  def spec = suite("suite")(
    test("test") {

      semanticdb.Locator(paths): (p, payload: TextDocuments) =>
        for {
          td <- payload.documents
          si <- td.symbols
        } do
          println(si)

      // IDEA: construct a btree with the line/char positions of each definition
      // so that each reference can be located
//      symbols.foreach(println)

      assert(1)(equalTo(1))
    }
  )
}
