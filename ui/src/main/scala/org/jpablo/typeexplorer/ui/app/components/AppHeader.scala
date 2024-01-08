package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.jpablo.typeexplorer.shared.inheritance.Path
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.daisyui.Join
import org.jpablo.typeexplorer.ui.domUtils.dataTip
import org.jpablo.typeexplorer.ui.widgets.Dialog
import org.jpablo.typeexplorer.ui.widgets.Icons.*
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
  val titleDialog = TitleDialog(appState.activeProject.name, titleDialogOpen)
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
      div(
        cls     := "flex-none tooltip tooltip-bottom",
        dataTip := "Edit project name",
        button(
          cls := "btn btn-ghost btn-sm",
          onClick --> titleDialogOpen.set(true),
          child.text <--
            appState.activeProject.project.signal.map: p =>
              if p.name.isBlank then "Untitled" else p.name
        )
      ),
      div(
        cls := "divider divider-horizontal mx-1"
      ),
      // -------- project selector --------
      Join(
        div(
          cls     := "tooltip tooltip-bottom",
          dataTip := "Select project",
          a(
            cls := "btn btn-sm join-item",
            onClick --> projectSelector.showModal(),
            label.listIcon
          )
        ),
        // -------- new tab button --------
        div(
          cls     := "tooltip tooltip-bottom",
          dataTip := "New tab",
          a(
            cls := "btn btn-sm join-item",
            onClick --> appState.newPage(),
            label.folderPlusIcon
          )
//          )
        ),
        // -------- close tab button --------
        div(
          cls     := "tooltip tooltip-bottom",
          dataTip := "Close tab",
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
        div(
          cls     := "tooltip tooltip-bottom",
          dataTip := "Project settings",
          input(idAttr := "drawer-1", tpe := "checkbox", cls := "drawer-toggle"),
          label(
            forId := "drawer-1",
            cls   := "btn btn-sm drawer-button bi bi-gear"
          )
        )
      )
    ),
    titleDialog.tag,
    projectSelector.tag
  )
end AppHeader

def TitleDialog(title: Var[String], open: Var[Boolean]) =
  Dialog(
    cls := "modal",
    cls.toggle("modal-open") <-- open.signal,
    div(
      cls := "modal-box",
      input(
        tpe         := "text",
        cls         := "input input-bordered w-full",
        placeholder := "Project name",
        focus <-- open.signal.changes,
        controlled(value <-- title, onInput.mapToValue --> title),
        onKeyDown.filter(e => e.key == "Enter" || e.key == "Escape") --> open.set(false)
      ),
      div(
        cls := "modal-action",
        form(
          method := "dialog",
          button(cls := "btn", "close", onClick.mapTo(false) --> open.set)
        )
      )
    )
  )
