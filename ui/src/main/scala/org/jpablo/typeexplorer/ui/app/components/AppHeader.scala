package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.shared.inheritance.Path
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.domUtils.dataTip
import org.jpablo.typeexplorer.ui.widgets.Icons.*

enum DiagramType:
  case Inheritance
  case CallGraph

def AppHeader(
  appState: AppState,
  selectedProject: EventBus[ProjectId],
  deleteProject: EventBus[ProjectId],
  findActiveTab: () => Int
): Div =
  val titleDialog = TitleDialog(appState.activeProject.name)
  val projectSelector = ProjectSelector(appState.projects, selectedProject, deleteProject)
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
        cls := "divider divider-horizontal mx-1",
      ),
      div(
        cls := "flex-none tooltip tooltip-bottom",
        dataTip := "Edit project name",
        button(
          cls := "btn btn-ghost btn-sm",
          onClick --> titleDialog.showModal(),
          child.text <--
            appState.activeProject.project.signal.map: p =>
              if p.name.isBlank then "Untitled" else p.name
        )
      ),
      div(
        cls := "divider divider-horizontal mx-1",
      ),
      // -------- project selector --------
      div(
        cls := "flex-none",
        div(
          cls := "tooltip tooltip-bottom",
          dataTip := "Select project",
          a(
            cls := "btn btn-sm",
            onClick --> projectSelector.showModal(),
            label.listIcon
          )
        ),
      ),
      // -------- new tab button --------
      div(
        cls := "flex-none",
        div(
          cls := "tooltip tooltip-bottom",
          dataTip := "New tab",
          a(
            cls := "btn btn-sm",
            onClick --> appState.newPage(),
            label.folderPlusIcon
          )
        ),
      ),
      // -------- close tab button --------
      div(
        cls := "flex-1",
        div(
          cls := "tooltip tooltip-bottom",
          dataTip := "Close tab",
          a(
            cls := "btn btn-sm",
            onClick --> { _ =>
              appState.closePage(findActiveTab())
            },
            label.folderMinusIcon
          )
        ),
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
          cls := "tooltip tooltip-bottom",
          dataTip := "Configuration",
          a(
            cls := "btn btn-sm",
            label(
              forId := "drawer-1",
              cls := "drawer-button bi bi-gear"
            )
          )
        )
      )
    ),
    titleDialog.tag,
    projectSelector.tag
  )
end AppHeader

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
