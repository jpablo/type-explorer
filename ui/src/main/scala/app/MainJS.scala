package app

import org.scalajs.dom
import org.scalajs.dom.document
import com.raquo.laminar.api.L._

object MainJS {
  def main(args: Array[String]): Unit =
    render(dom.document.querySelector("#show-file"), app.Layout.container)

}
