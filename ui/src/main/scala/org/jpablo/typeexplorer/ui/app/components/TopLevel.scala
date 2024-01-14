package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.widgets.{Button, small}
import org.jpablo.typeexplorer.ui.widgets.Icons.closeIcon
import com.raquo.laminar.api.features.unitArrows

def TopLevel(
    appState:        AppState,
    documents:       EventStream[List[TextDocumentsWithSource]],
    selectedProject: EventBus[ProjectId],
    deleteProject:   EventBus[ProjectId],
    errors:          EventBus[String]
) =
  div(
    cls := "te-parent-4",
    AppHeader(appState, selectedProject, deleteProject),
    TabsArea(appState, documents),
    AppFooter,
    AppConfigDialog(appState),
    ErrorToast(errors)
  )

def ErrorToast(messages: EventBus[String]) =
  val hidden = Var(true)
  div(
    messages.events --> hidden.set(false),
    cls := "toast toast-center",
    cls.toggle("hidden") <-- hidden.signal,
    div(
      cls := "alert alert-error",
      child.text <-- messages.events,
      Button(
        cls := "float-right btn-circle btn-error",
        onClick --> hidden.set(true),
        a.closeIcon
      ).small
    )
  )
