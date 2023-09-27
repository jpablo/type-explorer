package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{Project, ProjectId}
import org.scalajs.dom.HTMLDivElement

def ProjectSelector(
    projects: Signal[Map[ProjectId, Project]],
    selectedProject: EventBus[ProjectId]
) =
  dialog(
    cls := "modal",
    div(
      cls := "modal-box",
      input(
        tpe := "text",
        cls := "input input-bordered w-full",
        placeholder := "Select project"
      ),
      ul(
        cls := "menu",
        children <--
          projects.map { projects =>
            projects.map { case (id, project) =>
              li(
                cls := "menu-item",
                a(
                  cls := "text-gray-700",
                  href := "#",
                  onClick.preventDefault.mapTo(id) --> selectedProject,
                  if project.name.isBlank then id else project.name
                )
              )
            }.toList
          }
      ),
      div(
        cls := "modal-action",
        form(method := "dialog", button(cls := "btn", "close"))
      )
    )
  )
