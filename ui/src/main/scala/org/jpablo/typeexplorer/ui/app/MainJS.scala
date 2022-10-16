package org.jpablo.typeexplorer.ui.app

import org.jpablo.typeexplorer.ui.app.components
import com.raquo.laminar.api.L.render
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document

object MainJS {
  def main(args: Array[String]): Unit =
    render(document.querySelector("#app"), TopLevel)

}
