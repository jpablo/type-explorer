package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*
import zio.test.*
import zio.test.Assertion.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.backend.semanticdb.All
import java.nio.file.Paths
import scala.io.Source

object InheritanceDiagramSpec2 extends ZIOSpecDefault:
  def spec =
    suite("fromDocuments suite"):
      test("fromTextDocuments"):
        val path = Paths.get("backend/src/test/resources")
        val doc = All.scan(path.toAbsolutePath).map(_._2).head
        val diagram = InheritanceDiagram.fromTextDocumentsWithSource(???)
        val ns = diagram.namespaces.find(_.symbol.toString == "libretto/examples/canteen/Customer.").get
        val names = ns.methods.map(_.displayName)
        // ensure some internal names are not present in the output
        assertTrue(names forall (!_.contains("<init>"))) && assertTrue(names forall (!_.contains("$default$")))
