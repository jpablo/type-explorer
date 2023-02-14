package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models

case class PackagesOptions (
  onlyActive: Boolean = false,
  onlyTests : Boolean = false,
  nsKind    : Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
)

case class AppConfig (
  devMode         : Boolean         = false,
  packagesOptions : PackagesOptions = PackagesOptions(),
  diagramOptions  : DiagramOptions  = DiagramOptions()
)

object AppConfig:
  given JsonCodec[AppConfig] = DeriveJsonCodec.gen

object PackagesOptions:
  given JsonCodec[PackagesOptions] = DeriveJsonCodec.gen

