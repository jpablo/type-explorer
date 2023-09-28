package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{Project, ProjectId}
import org.scalajs.dom.{HTMLDialogElement, HTMLDivElement}
import com.raquo.laminar.api.features.unitArrows

val dialog = htmlTag[HTMLDialogElement]("dialog")

case class Dialog(mods: Modifier[ReactiveHtmlElement.Base]*):
  export tag.ref, tag.ref.showModal

  val tag = dialog(mods)

def ProjectSelector(
    projects: Signal[Map[ProjectId, Project]],
    selectedProject: EventBus[ProjectId],
    deleteProject: EventBus[ProjectId]
) =
  val filter = Var("")
  val filteredProjects =
    projects
      .combineWith(filter.signal)
      .map { (projects, filter) =>
        if filter.isBlank then projects
        else
          projects.filter { case (_, project) =>
            project.name.toLowerCase.contains(
              filter.toLowerCase
            ) || project.id.value.toLowerCase.contains(filter.toLowerCase)
          }
      }

  Dialog(
    cls := "modal",
    div(
      cls := "modal-box",
      input(
        tpe := "text",
        cls := "input input-bordered w-full",
        placeholder := "Filter project",
        controlled(
          value <-- filter,
          onInput.mapToValue --> filter
        )
      ),
      ul(
        cls := "menu",
        children <-- filteredProjects.map(
          _.map(ProjectSelectorItem(selectedProject, deleteProject)).toSeq
        )
      ),
      div(
        cls := "modal-action",
        form(
          method := "dialog",
          button(
            cls := "btn",
            "new",
            onClick.preventDefault --> selectedProject.emit(ProjectId.random)
          ),
          button(cls := "btn", "close")
        )
      )
    )
  )

def ProjectSelectorItem(
    selectedProject: EventBus[ProjectId],
    deleteProject: EventBus[ProjectId]
)(id: ProjectId, project: Project) =
  li(
    cls := "menu-item",
    a(
      cls := "text-gray-700",
      href := "#",
      onClick.preventDefault.mapTo(id) --> selectedProject,
      if project.name.isBlank then id.value else project.name
    ),
    button(
      cls := "btn btn-sm btn-error",
      onClick.preventDefault.mapTo(id) --> deleteProject,
      "delete"
    )
  )
