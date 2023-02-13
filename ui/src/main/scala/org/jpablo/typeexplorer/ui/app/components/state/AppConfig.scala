package org.jpablo.typeexplorer.ui.app.components.state

import zio.json.*
import org.jpablo.typeexplorer.shared.models

case class PackagesOptions (
  onlyActive: Boolean = false,
  onlyTests : Boolean = false,
  nsKind    : Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
)

case class AppConfig (
  devMode   : Boolean = false,
  packagesOptions: PackagesOptions = PackagesOptions()
)

object AppConfig:
  given JsonCodec[AppConfig] = DeriveJsonCodec.gen

object PackagesOptions:
  given JsonCodec[PackagesOptions] = DeriveJsonCodec.gen

