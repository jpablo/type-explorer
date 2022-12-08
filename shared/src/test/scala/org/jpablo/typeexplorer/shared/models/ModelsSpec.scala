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
            ["com/raquo/airstream/core/EventStream#", ["Parents", "Children"]]
          ],
          "options": {
              "fields": false,
              "signatures": false
          }
      }
      """
      val expected =
        InheritanceReq(
          List("/Users/jpablo/GitHub/Airstream"),
//          Set(models.Symbol("com/raquo/airstream/core/EventStream#") -> Set(Parents, Children))
          Set(models.Symbol("com/raquo/airstream/core/EventStream#"))
        )

      assertTrue(json.fromJson[InheritanceReq] == Right(expected))
    },

    test("Serialize InheritanceReq without options") {
      val json = """
      {
          "paths": [
            "/Users/jpablo/GitHub/Airstream"
          ],
          "symbols": [
            ["com/raquo/airstream/core/EventStream#", ["Parents", "Children"]]
          ]
      }
      """
      val expected =
        InheritanceReq(
          List("/Users/jpablo/GitHub/Airstream"),
//          Set(models.Symbol("com/raquo/airstream/core/EventStream#") -> Set(Parents, Children))
          Set(models.Symbol("com/raquo/airstream/core/EventStream#"))
        )

      assertTrue(json.fromJson[InheritanceReq] == Right(expected))
    }
  )


}

