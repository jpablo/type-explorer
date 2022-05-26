package app

import com.raquo.laminar.api.L.*

object RightColumn {
  def rightColumn =
    div (cls := "col", idAttr := "te-right-column",
      p ("Properties panel")
    )
}
