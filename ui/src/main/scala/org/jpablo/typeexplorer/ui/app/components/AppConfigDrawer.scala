package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.Project
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models

case class Updater[A](signal: Signal[A], update: A => Unit)

def AppConfigDrawer(projectConfig: Var[Project]) =

  def updater[A, B, C](va: Var[A], modifyField: PathLazyModify[A, B])(to: A => C, from: C => B) =
    Updater(
      signal = va.signal.map(a => to(a)),
      update = (c: C) => va.update(a => modifyField.setTo(from(c))(a))
    )

  val basePathUpdater =
    updater(projectConfig, modifyLens(_.basePaths))(
      to = _.basePaths.mkString("\n"),
      from = _.split("\n").toList.map(Path.apply)
    )

  val hiddenFieldsUpdater =
    updater(projectConfig, modifyLens(_.diagramOptions.hiddenFields))(
      to = _.diagramOptions.hiddenFields.mkString("\n"),
      from = _.split("\n").toList
    )

  val hiddenSymbolsUpdater =
    updater(projectConfig, modifyLens(_.diagramOptions.hiddenSymbols))(
      to = _.diagramOptions.hiddenSymbols.mkString("\n"),
      from = _.split("\n").map(models.Symbol.apply).toList
    )

  val advancedModeUpdater =
    updater(projectConfig, modifyLens(_.advancedMode))(to = _.advancedMode, from = identity)

  div(cls := "drawer-side",
    label(cls := "drawer-overlay", forId := "drawer-1"),

    form(cls := "p-4 w-96 bg-base-100 text-base-content flex flex-col space-y-4 h-full",

      h1(cls := "drawer-title text-xl font-bold",
        "Settings"
      ),

      div(cls := "form-control",
        label(cls := "label", b(cls := "label-text", "basePath")),
        textArea(
          cls := "textarea textarea-bordered h-24 whitespace-nowrap",
          value <-- basePathUpdater.signal,
          onBlur.mapToValue --> basePathUpdater.update,
        )
      ),

      div(cls := "form-control",
        label(cls := "label", b(cls := "label-text", "Hidden fields")),
        textArea(
          cls := "textarea textarea-bordered h-24 whitespace-nowrap",
          controlled(value <-- hiddenFieldsUpdater.signal, onInput.mapToValue --> hiddenFieldsUpdater.update)
        )
      ),

      div(cls := "form-control",
        label(cls := "label", b(cls := "label-text", "Hidden symbols")),
        textArea(
          cls := "textarea textarea-bordered h-24 whitespace-nowrap",
          controlled(value <-- hiddenSymbolsUpdater.signal, onInput.mapToValue --> hiddenSymbolsUpdater.update)
        )
      ),

      div(cls := "form-control",
        label(forId := "dev-mode-id", cls := "label cursor-pointer",
          b(cls := "label-text pr-1", "Show semanticdb tab"),
          input(idAttr := "dev-mode-id", tpe := "checkbox", cls := "toggle toggle-xs",
            controlled(checked <-- advancedModeUpdater.signal, onClick.mapToChecked --> advancedModeUpdater.update)
          )
        )
      )
    )
  )

