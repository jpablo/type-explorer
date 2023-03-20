package org.jpablo.typeexplorer.shared.models

import zio.test.*
import zio.test.Assertion.*
import zio.Scope
import zio.json.*

import org.jpablo.typeexplorer.shared.webApp.InheritanceRequest
import org.jpablo.typeexplorer.shared.models

object ModelsSpec extends ZIOSpecDefault:

  override def spec = suite("Models Spec") (

    test("Serialize InheritanceReq"):
      val json = """
      {
          "paths": [
            "/Users/jpablo/GitHub/Airstream"
          ],
          "symbols": [
            ["com/raquo/airstream/core/EventStream#", null]
          ],
          "options": {
              "fields": false,
              "signatures": false
          }
      }
      """
      val expected =
        InheritanceRequest(
          List("/Users/jpablo/GitHub/Airstream"),
          List(models.Symbol("com/raquo/airstream/core/EventStream#") -> None)
        )
      assertTrue(json.fromJson[InheritanceRequest[String]] == Right(expected))
    ,
    test("Serialize InheritanceReq without options"):
      val json = """
      {
          "paths": [
            "/Users/jpablo/GitHub/Airstream"
          ],
          "symbols": [
            ["com/raquo/airstream/core/EventStream#", null]
          ]
      }
      """
      val expected =
        InheritanceRequest(
          List("/Users/jpablo/GitHub/Airstream"),
          List(models.Symbol("com/raquo/airstream/core/EventStream#") -> None)
        )

      assertTrue(json.fromJson[InheritanceRequest[String]] == Right(expected))
  )
