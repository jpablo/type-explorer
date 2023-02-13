package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.AppConfig

def AppConfigDrawer($appConfig: Var[AppConfig]) =
  div(cls := "drawer-side",
    label(cls := "drawer-overlay", forId := "drawer-1"),
    div(cls := "p-4 w-80 bg-base-100 text-base-content",

      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "excluded fields"),
          textArea(
            cls := "textarea textarea-bordered h-24",
          )
        )
      ),

      div(cls := "form-control",
        label(forId := "dev-mode-id", cls := "label cursor-pointer",
          span(cls := "label-text pr-1", "Developer mode"),
          input(idAttr := "dev-mode-id",
            tpe := "checkbox",
            cls := "toggle toggle-xs",
            controlled(
              checked <-- $appConfig.signal.map(_.devMode),
              onClick.mapToChecked --> { b =>
                $appConfig.update(_.copy(devMode = b))
              }
            )
          ),
        ),
      )


    )
  )

