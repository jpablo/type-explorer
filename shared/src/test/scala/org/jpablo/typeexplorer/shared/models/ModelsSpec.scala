package org.jpablo.typeexplorer.shared.models

import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.InheritanceRequest
import zio.json.*
import zio.test.Assertion.*
import zio.test.*

object ModelsSpec extends ZIOSpecDefault:

  override def spec = suite("Models Spec") (

    test("Serialize InheritanceReq"):
      val serialization = """
      {
        "paths": [
          "/Users/jpablo/GitHub/Airstream"
        ],
        "activeSymbols": [
          [
            "com/raquo/airstream/core/EventStream#",
            null
          ]
        ],
        "options": {
          "showFields": false,
          "showSignatures": false,
          "hiddenFields": [
            "canEqual",
            "copy",
            "equals",
            "hashCode",
            "productArity",
            "productElement",
            "productIterator",
            "productPrefix",
            "toString",
            "_1",
            "_2",
            "_3",
            "_4"
          ],
          "hiddenSymbols": []
        }
      }
      """
      val expected =
        InheritanceRequest(
          List("/Users/jpablo/GitHub/Airstream"),
          List(models.GraphSymbol("com/raquo/airstream/core/EventStream#") -> None)
        )
      assertTrue(Right(expected) == serialization.fromJson[InheritanceRequest[String]])
    ,
    test("Serialize InheritanceReq without options"):
      val serialization = """
      {
        "paths": [
          "/Users/jpablo/GitHub/Airstream"
        ],
        "activeSymbols": [
          [
            "com/raquo/airstream/core/EventStream#",
            null
          ]
        ],
        "options": {
          "showFields": false,
          "showSignatures": false,
          "hiddenFields": [
            "canEqual",
            "copy",
            "equals",
            "hashCode",
            "productArity",
            "productElement",
            "productIterator",
            "productPrefix",
            "toString",
            "_1",
            "_2",
            "_3",
            "_4"
          ],
          "hiddenSymbols": []
        }
      }
      """
      val expected =
        InheritanceRequest(
          List("/Users/jpablo/GitHub/Airstream"),
          List(models.GraphSymbol("com/raquo/airstream/core/EventStream#") -> None)
        )

      assertTrue(serialization.fromJson[InheritanceRequest[String]] == Right(expected))
  )
