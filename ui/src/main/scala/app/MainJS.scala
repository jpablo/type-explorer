package app

import org.scalajs.dom
import org.scalajs.dom.document
import com.raquo.laminar.api.L.*

object MainJS {
  def main(args: Array[String]): Unit =
    render(dom.document.querySelector("#app"), app.Layout.container)

}
