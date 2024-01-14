package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.jpablo.typeexplorer.shared.inheritance.Path
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.widgets.Icons.*
import org.jpablo.typeexplorer.ui.widgets.{Join, SimpleDialog, Tooltip, Button, small, ghost}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

enum DiagramType:
  case Inheritance
  case CallGraph

def AppHeader(
    appState:        AppState,
    selectedProject: EventBus[ProjectId],
    deleteProject:   EventBus[ProjectId]
): Div =
  val titleDialogOpen = Var(false)
  val projectSelector =
    ProjectSelector(appState.projects, selectedProject, deleteProject)
  div(
    cls := "border-b border-slate-300",
    div(
      cls := "navbar bg-base-100",
      div(
        cls := "flex-none",
        a(cls := "btn btn-ghost normal-case text-xl", "Type Explorer")
      ),
      // -------- project title --------
      div(
        cls := "divider divider-horizontal mx-1"
      ),
      Tooltip(
        text = "Edit project name",
        Button(
          onClick --> titleDialogOpen.set(true),
          child.text <--
            appState.activeProject.project.signal.map: p =>
              if p.name.isBlank then "Untitled" else p.name
        ).small.ghost
      ).amend(cls := "flex-none"),
      div(
        cls := "divider divider-horizontal mx-1"
      ),
      // -------- project selector --------
      Join(
        Tooltip(
          text = "Select project",
          a(
            cls := "btn btn-sm join-item",
            onClick --> projectSelector.ref.showModal(),
            label.listIcon
          )
        ),
        // -------- new tab button --------
        Tooltip(
          text = "New tab",
          a(
            cls := "btn btn-sm join-item",
            onClick --> appState.newPage(),
            label.folderPlusIcon
          )
//          )
        ),
        // -------- close tab button --------
        Tooltip(
          text = "Close tab",
          a(
            cls := "btn btn-sm join-item",
            onClick --> appState.closeActivePage(),
            label.folderMinusIcon
          )
        )
      ).amend(
        cls := "flex-1"
      ),
      // -------- base path --------
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
        // -------- config gear button --------
        Tooltip(
          text = "Project settings",
          button(
            cls := "btn btn-sm btn-ghost drawer-button bi bi-gear",
            onClick --> appState.appConfigDialogOpenV.set(true)
          )
        )
      )
    ),
    TitleDialog(appState.activeProject.name, titleDialogOpen),
    projectSelector
  )
end AppHeader

def TitleDialog(title: Var[String], open: Var[Boolean]) =
  SimpleDialog(
    open,
    input(
      tpe         := "text",
      cls         := "input input-bordered w-full",
      placeholder := "Project name",
      focus <-- open.signal.changes,
      controlled(value <-- title, onInput.mapToValue --> title),
      onKeyDown.filter(e => e.key == "Enter" || e.key == "Escape") --> open.set(false)
    )
  )
