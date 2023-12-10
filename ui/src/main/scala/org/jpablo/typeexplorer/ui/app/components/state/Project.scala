package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, PackagesOptions, ProjectSettings}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import zio.json.*


case class Project(
    id: ProjectId,
    name: String = "",
    advancedMode: Boolean = false,
    packagesOptions: PackagesOptions = PackagesOptions(),
    projectSettings: ProjectSettings = ProjectSettings(),
    pages: Vector[Page] = Vector(Page()),
    activePage: Int = 0
) derives JsonCodec

case class Page(
    // This can't be a Map[A, Option[B]], as zio-json will remove entries with None values
    activeSymbols: ActiveSymbolsSeq = List.empty,
    diagramOptions: DiagramOptions = DiagramOptions()
) derives JsonCodec
