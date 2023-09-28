package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{Project, ProjectId}
import org.scalajs.dom.{HTMLDialogElement, HTMLDivElement}
import com.raquo.laminar.api.features.unitArrows
import org.jpablo.typeexplorer.ui.app.toggle

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
  val selection = Var(Set.empty[ProjectId])
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
      // -------- filter ---------
      input(
        tpe := "text",
        cls := "input input-bordered w-full",
        placeholder := "Filter project",
        controlled(
          value <-- filter,
          onInput.mapToValue --> filter
        )
      ),
      // -------- project list ---------
      ul(
        cls := "menu",
        children <-- filteredProjects.map(
          _.map(ProjectSelectorItem(selectedProject, selection)).toSeq
        )
      ),
      // -------- actions ---------
      div(
        cls := "modal-action",
        form(
          method := "dialog",
          button(
            cls := "btn",
            "new",
            onClick.preventDefault --> selectedProject.emit(ProjectId.random)
          ),
          button(
            cls := "btn",
            "delete",
            onClick.preventDefault
              .compose(_.sample(selection)) --> (_.foreach(deleteProject.emit))
          ),
          button(cls := "btn", "close")
        )
      )
    )
  )

def ProjectSelectorItem(
    selectedProject: EventBus[ProjectId],
    selection: Var[Set[ProjectId]]
)(id: ProjectId, project: Project) =
  li(
    cls := "flex flex-row",
    a(
      cls := "text-gray-700 flex-1",
      href := "#",
      onClick.preventDefault.mapTo(id) --> selectedProject,
      if project.name.isBlank then id.value else project.name
    ),
    input(
      cls := "checkbox flex-none",
      tpe := "checkbox",
      onClick.preventDefault.mapTo(id) --> selection.update(_.toggle(id))
    )
  )