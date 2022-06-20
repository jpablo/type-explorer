package org.jpablo.typeexplorer.app

import org.jpablo.typeexplorer.app.components
import com.raquo.laminar.api.L.render
import org.scalajs.dom.document

object MainJS {
  def main(args: Array[String]): Unit =
    render(document.querySelector("#app"), components.TopLevel.topLevel)

}
