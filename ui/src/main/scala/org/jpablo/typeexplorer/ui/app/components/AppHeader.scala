package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.ProjectVar
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom
import org.scalajs.dom.HTMLDialogElement

enum DiagramType:
  case Inheritance
  case CallGraph

val dialog = htmlTag("dialog")

def AppHeader(basePaths: Signal[List[Path]], activeProject: ProjectVar): Div =
  val titleDialog = TitleDialog(activeProject.name)
  div(
    cls := "border-b border-slate-300",
    div(
      cls := "navbar bg-base-100",
      div(
        cls := "flex-none",
        a(cls := "btn btn-ghost normal-case text-xl", "Type Explorer")
      ),
      div(
        cls := "flex-1",
        button(
          cls := "btn btn-ghost btn-sm",
          onClick --> (titleDialog.ref: HTMLDialogElement).showModal(),
          child.text <--
            activeProject.project.signal.map: p =>
              if p.name.isBlank then "Untitled" else p.name
        )
      ),
      div(
        cls := "flex-none gap-3",
        b("base path:"),
        span(
          child.text <--
            basePaths.map: (ps: List[Path]) =>
              ps.headOption
                .map(_.toString)
                .getOrElse("None") + (if ps.size > 1 then s" (+${ps.size - 1})"
                                      else "")
        ),
        a(
          cls := "btn btn-sm",
          label(
            forId := "drawer-1",
            cls := "drawer-button bi bi-gear"
          )
        )
      )
    ),
    titleDialog
  )

def TitleDialog(title: Var[String]) =
  dialog(
    idAttr := "project-name-modal",
    cls := "modal",
    div(
      cls := "modal-box",
      input(
        tpe := "text",
        cls := "input input-bordered w-full",
        placeholder := "Project name",
        controlled(
          value <-- title.signal,
          onInput.mapToValue --> title.writer
        )
      ),
      div(
        cls := "modal-action",
        form(method := "dialog", /*cls := "modal-backdrop", */ button("close"))
      )
    )
  )
