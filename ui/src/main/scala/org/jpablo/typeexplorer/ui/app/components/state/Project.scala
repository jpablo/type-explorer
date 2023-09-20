package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols

// in memory app state
case class ProjectBuilder(
    projectsJson: StoredString // global (Projects)
)(using Owner):

  val projects: Var[Projects] =
    persistent(projectsJson, Projects())

  def currentProject: Project = ???

end ProjectBuilder

object ProjectBuilder:

  // 1. Load Projects
  // 2. Load active project (ProjectConfig)
  // 3. ProjectConfig (json) -> AppState (in memory)

  def build(fetchDiagram: List[Path] => Signal[InheritanceDiagram]): Project =
    given owner: Owner = OneTimeOwner(() => ())

    val projectsJson = storedString("projects", initial = "{}")

    // previous name: appState0.config
    val projects: Var[Projects] =
      persistent(projectsJson, Projects())

    val basePaths: Signal[List[Path]] =
      projects.signal.map(_.activeProject.basePaths)

    val projectConfig: Var[ProjectConfig] =
      projects
        .zoom(_.activeProject) { (projects, projectConfig) =>
          Projects(
            projects.projectConfigs + (projectConfig.id -> projectConfig),
            Some(projectConfig.id)
          )
        }

    val activeSymbols: Var[ActiveSymbols] =
      projects
        .zoom(_.activeProject.activeSymbols.toMap): (projects, activeSymbols) =>
          val projectConfig: ProjectConfig =
            projects.activeProject
              .modify(_.activeSymbols)
              .setTo(activeSymbols.toList)
          Projects(
            projectConfigs =
              projects.projectConfigs + (projectConfig.id -> projectConfig),
            activeProjectId = Some(projectConfig.id)
          )

    Project(
      inheritanceTabState = InheritanceTabState(
        activeSymbolsR = activeSymbols,
        fullInheritanceDiagramR = basePaths.flatMap(fetchDiagram)
      ),
      basePaths,
      projectConfig,
      projects
    )

// in memory state
case class Project(
    inheritanceTabState: InheritanceTabState,
    basePaths: Signal[List[Path]],
    config: Var[ProjectConfig],
    projects: Var[Projects]
):
  def updateAppConfig(f: ProjectConfig => ProjectConfig): Unit =
    config.update(f)
