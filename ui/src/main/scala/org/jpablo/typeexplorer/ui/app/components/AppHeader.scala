package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}

enum DiagramType:
  case Inheritance
  case CallGraph


def AppHeader(
  appState: AppState,
  selectedProject: EventBus[org.jpablo.typeexplorer.ui.app.components.state.ProjectId],
  deleteProject: EventBus[org.jpablo.typeexplorer.ui.app.components.state.ProjectId],
): Div =
  val titleDialog = TitleDialog(appState.activeProject.name)
  val projects = appState.persistedAppState.signal.map(_.projects)
  val projectSelector = ProjectSelector(projects, selectedProject, deleteProject)
  div(
    cls := "border-b border-slate-300",
    div(
      cls := "navbar bg-base-100",
      div(
        cls := "flex-none",
        a(cls := "btn btn-ghost normal-case text-xl", "Type Explorer")
      ),
      div(
        cls := "flex-none",
        button(
          cls := "btn btn-ghost btn-sm",
          onClick --> titleDialog.showModal(),
          child.text <--
            appState.activeProject.project.signal.map: p =>
              if p.name.isBlank then "Untitled" else p.name
        )
      ),
      div(
        cls := "flex-1",
        a(
          cls := "btn btn-sm",
          onClick --> projectSelector.showModal(),
          label(cls := "bi bi-list")
        )

      ),
      div(
        cls := "flex-none gap-3",
        b("base path:"),
        span(
          child.text <--
            appState.basePaths.map: (ps: List[Path]) =>
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
    titleDialog.tag,
    projectSelector.tag
  )

def TitleDialog(title: Var[String]) =
  Dialog(
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
        form(method := "dialog", button(cls := "btn", "close"))
      )
    )
  )
