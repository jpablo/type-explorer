package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.storedString
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols

/** In-memory App State
  */
case class AppState(
    persistedAppStateR: Var[PersistedAppState],
    inheritanceTabState: InheritanceTabState = InheritanceTabState()
)(using Owner):
  // synchronized with local storage
  val activeProjectR: Var[Project] =
    persistedAppStateR
      .zoom(_.activeProject) { (persistedAppState, activeProject) =>
        persistedAppState
          .modify(_.projects)
          .using(_ + (activeProject.id -> activeProject))
          .modify(_.activeProjectId)
          .setTo(Some(activeProject.id))
      }

  val basePaths: Signal[List[Path]] =
    activeProjectR.signal.map(_.basePaths)

  // synchronized with local storage
  val activeSymbols: Var[ActiveSymbols] =
    activeProjectR
      .zoom(_.activeSymbols.toMap) { (activeProject, activeSymbols) =>
        activeProject
          .modify(_.activeSymbols)
          .setTo(activeSymbols.toList)
      }

  def updateActiveProject(f: Project => Project): Unit =
    activeProjectR.update(f)

end AppState

object AppState:
  // 1. Load Projects (from local storage)
  // 2. Load active project (ProjectConfig)
  // 3. ProjectConfig (json) -> Project (in memory)

  def load(fetchDiagram: List[Path] => Signal[InheritanceDiagram]): AppState =
    given owner: Owner = OneTimeOwner(() => ())

    val appState =
      AppState(
        persistent(
          storedString("persistedAppState", initial = "{}"),
          initial = PersistedAppState()
        )
      )

    // in memory app state based on the active project id
    appState.copy(
      inheritanceTabState = appState.inheritanceTabState
        .modify(_.activeSymbolsR)
        .setTo(appState.activeSymbols)
        .modify(_.fullInheritanceDiagramR)
        .setTo(appState.basePaths.flatMap(fetchDiagram))
    )
