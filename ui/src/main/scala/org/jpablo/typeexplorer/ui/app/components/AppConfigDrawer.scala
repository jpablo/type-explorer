package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.AppConfig
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models

case class Updater[A](signal: Signal[A], update: A => Unit)

def AppConfigDrawer($appConfig: Var[AppConfig]) =

  def updater[A, B, C](va: Var[A], modifyField: PathLazyModify[A, B])(to: A => C, from: C => B) =
    Updater(
      signal = va.signal.map(a => to(a)),
      update = (c: C) => va.update(a => modifyField.setTo(from(c))(a))
    )

  val basePathUpdater =
    updater($appConfig, modifyLens(_.basePaths))(
      to = _.basePaths.mkString("\n"),
      from = _.split("\n").toList.map(Path.apply)
    )

  val hiddenFieldsUpdater =
    updater($appConfig, modifyLens(_.diagramOptions.hiddenFields))(
      to = _.diagramOptions.hiddenFields.mkString("\n"),
      from = _.split("\n").toList
    )

  val hiddenSymbolsUpdater =
    updater($appConfig, modifyLens(_.diagramOptions.hiddenSymbols))(
      to = _.diagramOptions.hiddenSymbols.mkString("\n"),
      from = _.split("\n").map(models.Symbol.apply).toList
    )

  val advancedModeUpdater =
    updater($appConfig, modifyLens(_.advancedMode))(to = _.advancedMode, from = identity)

  div(cls := "drawer-side",
    label(cls := "drawer-overlay", forId := "drawer-1"),
    div(cls := "p-4 w-80 bg-base-100 text-base-content",
      h1(cls := "drawer-title", "Settings"),
      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "basePath"),
          textArea(
            rows := 10,
            cols := 30,
            cls := "textarea textarea-bordered h-24 whitespace-nowrap",
            controlled(value <-- basePathUpdater.signal, onInput.mapToValue --> basePathUpdater.update)
          )
        )
      ),
      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "Hidden fields"),
          textArea(
            cls := "textarea textarea-bordered h-24 whitespace-nowrap",
            controlled(value <-- hiddenFieldsUpdater.signal, onInput.mapToValue --> hiddenFieldsUpdater.update)
          )
        )
      ),
      div(
        cls := "form-control",
        label(cls := "label", span(cls := "label-text", "Hidden symbols"),
          textArea(
            cls := "textarea textarea-bordered h-24 whitespace-nowrap",
            controlled(value <-- hiddenSymbolsUpdater.signal, onInput.mapToValue --> hiddenSymbolsUpdater.update)
          )
        )
      ),
      div(cls := "form-control",
        label(forId := "dev-mode-id", cls := "label cursor-pointer",
          span(cls := "label-text pr-1", "Show semanticdb tab"),
          input(idAttr := "dev-mode-id",
            tpe := "checkbox",
            cls := "toggle toggle-xs",
            controlled(checked <-- advancedModeUpdater.signal, onClick.mapToChecked --> advancedModeUpdater.update)
          )
        )
      )
    )
  )

