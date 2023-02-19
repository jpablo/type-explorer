package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models
import InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import org.jpablo.typeexplorer.ui.app.Path

case class PackagesOptions (
  onlyActive: Boolean = false,
  onlyTests : Boolean = false,
  nsKind    : Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
)

case class AppConfig (
  devMode         : Boolean         = false,
  packagesOptions : PackagesOptions = PackagesOptions(),
  diagramOptions  : DiagramOptions  = DiagramOptions(),
  basePaths       : List[Path]    = List.empty,
  allActiveSymbols: Map[Path, ActiveSymbolsSeq]  = Map.empty
)

object AppConfig:
  given JsonCodec[AppConfig] = DeriveJsonCodec.gen

object PackagesOptions:
  given JsonCodec[PackagesOptions] = DeriveJsonCodec.gen

