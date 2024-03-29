package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.backend.textDocuments.readTextDocumentsWithSource
import zio.test.*
import zio.test.Assertion.*

import java.nio.file.Paths

object InheritanceGraphSpec2 extends ZIOSpecDefault:
  def spec =
    suite("fromDocuments suite"):
      test("fromTextDocuments"):
        val path = Paths.get("backend/src/test/resources")
        for docs <- readTextDocumentsWithSource(List(path.toAbsolutePath)) yield
          val diagram = InheritanceGraph.from(docs)
          val ns = diagram.namespaces.find(_.symbol.toString == "libretto/examples/canteen/Customer.")
          val names = ns.get.methods.map(_.displayName)
          // ensure some internal names are not present in the output
          assertTrue(
            names forall (!_.contains("<init>")),
            names forall (!_.contains("$default$"))
          )
