package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.storedString
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, Path}

/** In-memory App State
  */
class AppState(
    persistedAppState: PersistentVar[PersistedAppState],
    projectId: ProjectId,
    fetchFullInheritanceGraph: List[Path] => Signal[InheritanceGraph]
)(using Owner):
  export activeProject.{
    basePaths,
    packagesOptions,
    diagramOptions,
    advancedMode,
    pages,
    newPage,
    setActivePage,
    closePage,
    update as updateActiveProject
  }

  val activeProject: ActiveProject =
    AppState.selectOrCreateProject(persistedAppState, projectId)

  val fullGraph: Signal[InheritanceGraph] =
    activeProject.basePaths.flatMap(fetchFullInheritanceGraph)

  val tabStates: Signal[Vector[InheritanceTabState]] =
    activeProject.pages
      .map: pages =>
        Vector
          .tabulate(pages.length)(activeProject.pageV)
          .map(InheritanceTabState(fullGraph, Var(Set.empty)))

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
    AppState(
      persistentVar(
        storedString("persistedAppState", initial = "{}"),
        initial = PersistedAppState()
      ),
      projectId,
      fetchFullInheritanceGraph
    )

  /** Select an existing project or create a new one. The new project will be
    * persisted only after the first update.
    */
  private def selectOrCreateProject(
      persistedAppState: PersistentVar[PersistedAppState],
      projectId: ProjectId
  )(using Owner): ActiveProject =
    ActiveProject {
      PersistentVar {
        persistedAppState
          .zoom(_.selectOrCreateProject(projectId)):
            (persistedAppState, selectedProject) =>
              persistedAppState
                .modify(_.projects)
                .using(_ + (selectedProject.id -> selectedProject))
      }
    }

end AppState
