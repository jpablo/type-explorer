package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.{PersistentVar, Project}
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.Path
import org.jpablo.typeexplorer.shared.models

case class Updater[A](signal: Signal[A], update: A => Unit)

def AppConfigDrawer(project: PersistentVar[Project]) =

  def updater[A, B, C](
      va:          Var[A],
      modifyField: PathLazyModify[A, B]
  )(to: A => C, from: C => B) =
    Updater(
      signal = va.signal.map(to),
      update = (c: C) => va.update(a => modifyField.setTo(from(c))(a))
    )

  val basePathUpdater =
    updater(project.v, modifyLens(_.projectSettings.basePaths))(
      to   = _.projectSettings.basePaths.mkString("\n"),
      from = _.split("\n").toList.map(Path.apply)
    )

  val hiddenFieldsUpdater =
    updater(project.v, modifyLens(_.projectSettings.hiddenFields))(
      to   = _.projectSettings.hiddenFields.mkString("\n"),
      from = _.split("\n").toList
    )

  val hiddenSymbolsUpdater =
    updater(project.v, modifyLens(_.projectSettings.hiddenSymbols))(
      to   = _.projectSettings.hiddenSymbols.mkString("\n"),
      from = _.split("\n").map(models.GraphSymbol.apply).toList
    )

  val advancedModeUpdater =
    updater(project.v, modifyLens(_.advancedMode))(
      to   = _.advancedMode,
      from = identity
    )

  form(
    cls := "p-4 w-96 bg-base-100 text-base-content flex flex-col space-y-4 h-full",
    h1(cls := "drawer-title text-xl font-bold", "Settings"),
    // --- base path ---
    div(
      cls := "form-control",
      label(cls := "label", b(cls := "label-text", "basePath")),
      textArea(
        cls := "textarea textarea-bordered h-32 whitespace-nowrap",
        value <-- basePathUpdater.signal,
        onBlur.mapToValue --> basePathUpdater.update
      )
    ),
    // --- Hidden fields ---
    div(
      cls := "form-control",
      label(cls := "label", b(cls := "label-text", "Hidden fields")),
      textArea(
        cls := "textarea textarea-bordered h-24 whitespace-nowrap",
        controlled(
          value <-- hiddenFieldsUpdater.signal,
          onInput.mapToValue --> hiddenFieldsUpdater.update
        )
      )
    ),
    // --- Hidden symbols ---
    div(
      cls := "form-control",
      label(cls := "label", b(cls := "label-text", "Hidden symbols")),
      textArea(
        cls := "textarea textarea-bordered h-24 whitespace-nowrap",
        controlled(
          value <-- hiddenSymbolsUpdater.signal,
          onInput.mapToValue --> hiddenSymbolsUpdater.update
        )
      )
    ),
    // --- semantic db ---
    div(
      cls := "form-control",
      label(
        forId := "dev-mode-id",
        cls   := "label cursor-pointer",
        b(cls := "label-text pr-1", "Show semanticdb tab"),
        input(
          idAttr := "dev-mode-id",
          tpe    := "checkbox",
          cls    := "toggle toggle-xs",
          controlled(
            checked <-- advancedModeUpdater.signal,
            onClick.mapToChecked --> advancedModeUpdater.update
          )
        )
      )
    )
  )
