package app

import org.scalajs.dom.document
import com.raquo.laminar.api.L.render

object MainJS {
  def main(args: Array[String]): Unit =
    render(document.querySelector("#app"), app.Layout.container)

}
