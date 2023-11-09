package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.storedString
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, Path}
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols

/** In-memory App State
  */
class AppState(
    persistedAppState: Var[PersistedAppState],
    val activeProject: ProjectVar, // = f(persistedAppState)
    val fullGraph: Signal[InheritanceGraph], // = f(activeProject)
    val tabStates: Vector[InheritanceTabState] = Vector.empty // = f(activeProject)
):
  export activeProject.{
    basePaths,
    packagesOptions,
    diagramOptions,
    advancedMode,
    update as updateActiveProject
  }

  def deleteProject(projectId: ProjectId): Unit =
    persistedAppState.update(_.deleteProject(projectId))

  val projects: Signal[Map[ProjectId, Project]] =
    persistedAppState.signal.map(_.projects)

object AppState:

  /** Load Projects from local storage and select the active project
    */
  def load(
      fetchFullInheritanceGraph: List[Path] => Signal[InheritanceGraph],
      projectId: ProjectId
  )(using Owner): AppState =
    val persistedAppState =
      persistentVar(
        storedString("persistedAppState", initial = "{}"),
        initial = PersistedAppState()
      )
    val activeProjectV: ProjectVar =
      selectOrCreateProject(persistedAppState, projectId)

    val activeProject = activeProjectV.project.now()

    val fullGraph: Signal[InheritanceGraph] =
      fetchFullInheritanceGraph(activeProject.projectSettings.basePaths)

    val tabStates: Vector[InheritanceTabState] =
      activeProject.pages.zipWithIndex.map: (p, i) =>
        InheritanceTabState(
          page = activeProjectV.page(i),
          fullGraph = fullGraph
        )

    AppState(
      persistedAppState,
      activeProjectV,
      fullGraph,
      tabStates
    )

  /** Select an existing project or create a new one. The new project will be
    * persisted only after the first update.
    */
  def selectOrCreateProject(
      persistedAppState: Var[PersistedAppState],
      projectId: ProjectId
  )(using Owner): ProjectVar =
    ProjectVar {
      persistedAppState
        .zoom(_.selectOrCreateProject(projectId)) {
          (persistedAppState, selectedProject) =>
            persistedAppState
              .modify(_.projects)
              .using(_ + (selectedProject.id -> selectedProject))
        }
    }

end AppState
