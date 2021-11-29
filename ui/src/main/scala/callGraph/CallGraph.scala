package callGraph

import java.net.URI
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.TextDocuments
import java.nio.file.{Files, Paths}

object CallGraph {

  val sampleData =
    List("")

  val paths = Paths.get(new URI("file:///Users/jpablo/code/Event-Horizon-API/target/scala-2.12/classes/META-INF/semanticdb/app/controllers/QueryController.scala.semanticdb"))

  def load =
    semanticdb.Locator(paths) { (p, payload: TextDocuments) =>
      for {
        td <- payload.documents
        si <- td.symbols
      } do
        println(si)
//        println(JsonFormat.toJsonString(si))
    }

}
