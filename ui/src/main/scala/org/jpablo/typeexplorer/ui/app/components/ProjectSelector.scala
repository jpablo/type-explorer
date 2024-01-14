package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{Project, ProjectId}
import org.jpablo.typeexplorer.ui.extensions.*
import org.jpablo.typeexplorer.ui.widgets.Dialog
import org.scalajs.dom.HTMLDivElement

def ProjectSelector(
    projects:        Signal[Map[ProjectId, Project]],
    selectedProject: EventBus[ProjectId],
    deleteProject:   EventBus[ProjectId]
) =
  val filter = Var("")
  val selection = Var(Set.empty[ProjectId])
  val filteredProjects =
    projects
      .combineWith(filter.signal)
      .map: (projects, filter) =>
        if filter.isBlank then projects
        else
          projects.filter: (_, project) =>
            project.name.toLowerCase
              .contains(filter.toLowerCase) || project.id.value.toLowerCase
              .contains(filter.toLowerCase)

  Dialog()(
    // -------- filter ---------
    input(
      tpe         := "text",
      cls         := "input input-bordered w-full",
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
        _.map(ProjectSelectorItem(selection)).toSeq
      )
    )
  )(
    button(cls := "btn", "new", onClick.preventDefault --> selectedProject.emit(ProjectId.random)),
    button(cls := "btn", "delete", onClick.preventDefault.compose(_.sample(selection)) --> (_.foreach(deleteProject.emit))),
    button(cls := "btn", "close")
  )

def ProjectSelectorItem(
    selection: Var[Set[ProjectId]]
)(id: ProjectId, project: Project) =
  li(
    cls := "flex flex-row",
    a(
      cls  := "text-gray-700 flex-1",
      href := s"/${id.value}",
      if project.name.isBlank then id.value else project.name
    ),
    div(
      cls := "flex-none",
      input(
        cls := "checkbox",
        tpe := "checkbox",
        controlled(
          checked <-- selection.signal.map(_.contains(id)),
          onClick.mapTo(id) --> selection.update(_.toggle(id))
        )
      )
    )
  )
