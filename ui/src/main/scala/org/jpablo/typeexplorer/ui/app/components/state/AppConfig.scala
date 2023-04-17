package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.DiagramOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models
import InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import org.jpablo.typeexplorer.ui.app.Path

case class PackagesOptions (
  onlyActive: Boolean = false,
  onlyTests : Boolean = false,
  nsKind    : Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
) derives JsonCodec

case class AppConfig (
  advancedMode   : Boolean          = false,
  packagesOptions: PackagesOptions  = PackagesOptions(),
  diagramOptions : DiagramOptions   = DiagramOptions(),
  basePaths      : List[Path]       = List.empty,
  // This can't be a Map[A, Option[B]], as zio-json will remove entries with None values
  activeSymbols  : ActiveSymbolsSeq = List.empty
) derives JsonCodec

