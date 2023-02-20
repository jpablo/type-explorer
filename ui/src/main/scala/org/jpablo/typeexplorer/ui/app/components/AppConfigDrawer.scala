package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.AppConfig
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.app.Path

def AppConfigDrawer($appConfig: Var[AppConfig]) =
  div(cls := "drawer-side",
    label(cls := "drawer-overlay", forId := "drawer-1"),
    div(cls := "p-4 w-80 bg-base-100 text-base-content",
      h1(cls := "drawer-title", "Settings"),

      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "basePath"),
          textArea(
            cls := "textarea textarea-bordered h-24 whitespace-nowrap",
            child.text <--
              $appConfig.signal.map(_.basePaths.mkString("\n")),
            onInput.mapToValue --> { v =>
              $appConfig.update(_.modify(_.basePaths).setTo(v.split("\n").toList.map(Path.apply)))
            }
          )
        )
      ),

      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "Hidden fields"),
          textArea(
            cls := "textarea textarea-bordered h-24",
            child.text <--
              $appConfig.signal.map(_.diagramOptions.hiddenFields.mkString("\n")),
            onInput.mapToValue --> { v =>
              $appConfig.update(_.modify(_.diagramOptions.hiddenFields).setTo(v.split("\n").toList))
            }
          )
        )
      ),

      div(cls := "form-control",
        label(forId := "dev-mode-id", cls := "label cursor-pointer",
          span(cls := "label-text pr-1", "Show semanticdb tab"),
          input(idAttr := "dev-mode-id",
            tpe := "checkbox",
            cls := "toggle toggle-xs",
            controlled(
              checked <-- $appConfig.signal.map(_.advancedMode),
              onClick.mapToChecked --> { b =>
                $appConfig.update(_.copy(advancedMode = b))
              }
            )
          ),
        ),
      )


    )
  )

