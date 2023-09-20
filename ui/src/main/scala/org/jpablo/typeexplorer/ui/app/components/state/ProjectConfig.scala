package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.DiagramOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import org.jpablo.typeexplorer.ui.app.Path

case class PackagesOptions(
    onlyActive: Boolean = false,
    onlyTests: Boolean = false,
    nsKind: Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
) derives JsonCodec


type ProjectId = String

// persisted
case class Projects(
    projectConfigs: Map[ProjectId, ProjectConfig] = Map.empty,
    activeProjectId: Option[ProjectId] = None
) derives JsonCodec {
    def activeProject: ProjectConfig =
      activeProjectId.flatMap(projectConfigs.get).getOrElse(ProjectConfig("default")) // TODO: use a random UUID
}

// persisted
case class ProjectConfig(
    id: ProjectId,
    advancedMode: Boolean = false,
    packagesOptions: PackagesOptions = PackagesOptions(),
    diagramOptions: DiagramOptions = DiagramOptions(),
    basePaths: List[Path] = List.empty,
    // This can't be a Map[A, Option[B]], as zio-json will remove entries with None values
    activeSymbols: ActiveSymbolsSeq = List.empty
) derives JsonCodec

