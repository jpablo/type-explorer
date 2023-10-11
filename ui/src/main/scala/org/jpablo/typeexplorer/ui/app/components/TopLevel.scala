package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.jpablo.typeexplorer.ui.daisyui.{Button, small}
import org.jpablo.typeexplorer.ui.widgets.Icons

def TopLevel(
    appState: AppState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    documents: EventStream[List[TextDocumentsWithSource]],
    selectedProject: EventBus[ProjectId],
    deleteProject: EventBus[ProjectId]
) =
  val errors = new EventBus[String]
  setupErrorHandling(errors)
  div(
    ErrorToast(errors),
    cls := "drawer drawer-end",
    input(idAttr := "drawer-1", tpe := "checkbox", cls := "drawer-toggle"),
    div(
      cls := "drawer-content flex flex-col h-screen",
      AppHeader(appState, selectedProject, deleteProject),
      TabsArea(appState, inheritanceSvgDiagram, documents),
      AppFooter,
      // -------- advanced/debug mode --------
      appState.advancedMode.childWhenTrue:
        div(
          div(
            child.text <-- appState.inheritanceTab.canvasSelection.signal
              .map(ds => s"canvasSelection: ${ds.size}")
          ),
          div(
            child.text <-- appState.inheritanceTab.activeSymbols.signal
              .map(ss => s"activeSymbols: ${ss.size}")
          )
        )
    ),
    AppConfigDrawer(appState.activeProject.project)
  )

def setupErrorHandling(errors: EventBus[String]): Unit =
  AirstreamError.registerUnhandledErrorCallback(ex =>
    errors.emit(ex.getMessage)
  )
  windowEvents
    .apply(_.onError)
    .foreach { errorEvent =>
      errors.emit(errorEvent.message)
    }(owner = unsafeWindowOwner)

def ErrorToast(messages: EventBus[String]) =
  val hidden = Var(true)
  div(
    messages.events --> (_ => hidden.set(false)),
    cls := "toast toast-center",
    cls.toggle("hidden") <-- hidden.signal,
    div(
      cls := "alert alert-error",
      child.text <-- messages.events,
      Button(
        cls := "btn float-right btn-circle btn-error",
        onClick.mapTo(true) --> hidden,
        Icons.close
      ).small
    )
  )
