package org.jpablo.typeexplorer.shared.models

import zio.test.*
import zio.test.Assertion.*
import zio.Scope
import zio.json.*

import org.jpablo.typeexplorer.shared.webApp.InheritanceReq
import org.jpablo.typeexplorer.shared.inheritance.Related.*
import org.jpablo.typeexplorer.shared.models

object ModelsSpec extends ZIOSpecDefault {

  override def spec = suite("Models Spec") (
    test("serialize Related") {
      assertTrue(Parents.toJson == """"Parents"""") &&
      assertTrue(Children.toJson == """"Children"""")
    },
    test("Serialize InheritanceReq") {
      val json = """
      {
          "paths": [
            "/Users/jpablo/GitHub/Airstream"
          ],
          "symbols": [
            ["com%2Fraquo%2Fairstream%2Fcore%2FWritableEventStream%23", ["Parents", "Children"]]
          ]
      }      
      """

      val expected = InheritanceReq(
        List("/Users/jpablo/GitHub/Airstream"),
        Set(models.Symbol("com%2Fraquo%2Fairstream%2Fcore%2FWritableEventStream%23") -> Set(Parents, Children))
      )

      assertTrue(json.fromJson[InheritanceReq] == Right(expected))
    }
  )


}
  
