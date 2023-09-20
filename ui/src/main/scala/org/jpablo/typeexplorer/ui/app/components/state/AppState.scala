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

// in memory state
case class AppState(
    inheritanceTabState: InheritanceTabState = InheritanceTabState(),
    projects: Var[Projects] // <--
):
  given owner: Owner = OneTimeOwner(() => ())

  // synchronized with local storage
  val projectConfig: Var[ProjectConfig] =
    projects
      .zoom(_.activeProject) { (projects, projectConfig) =>
        Projects(
          projects.projectConfigs + (projectConfig.id -> projectConfig),
          Some(projectConfig.id)
        )
      }

  val basePaths: Signal[List[Path]] =
    projects.signal.map(_.activeProject.basePaths)

  // synchronized with local storage
  val activeSymbols: Var[ActiveSymbols] =
    projectConfig
      .zoom(_.activeSymbols.toMap) { (projectConfig, activeSymbols) =>
        projectConfig
          .modify(_.activeSymbols)
          .setTo(activeSymbols.toList)
      }


  def updateAppConfig(f: ProjectConfig => ProjectConfig): Unit =
    projectConfig.update(f)

object AppState:
  // 1. Load Projects (from local storage)
  // 2. Load active project (ProjectConfig)
  // 3. ProjectConfig (json) -> Project (in memory)

  def load(fetchDiagram: List[Path] => Signal[InheritanceDiagram]): AppState =
    given owner: Owner = OneTimeOwner(() => ())

    // load from local storage
    val projectsJson = storedString("projects", initial = "{}")

    // synchronized with local storage
    val projects: Var[Projects] =
      persistent(projectsJson, Projects())

    val project0 = AppState(projects = projects)
    // in memory app state based on the active project id
    project0
      .modify(_.inheritanceTabState.activeSymbolsR).setTo(project0.activeSymbols)
      .modify(_.inheritanceTabState.fullInheritanceDiagramR).setTo(project0.basePaths.flatMap(fetchDiagram))
