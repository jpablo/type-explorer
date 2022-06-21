package org.jpablo.typeexplorer.ui.experiments.laminar

import zio.test.*
import zio.test.Assertion.*
import com.raquo.laminar.api.L.{*, given}

object LaminarSpec extends ZIOSpecDefault {

  def spec = suite("LaminarSpec")(
    test("div") {
//      val d = div("a")
//      println(d)
      assert(1)(equalTo(1))
    }
  )
}
