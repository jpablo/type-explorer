package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*
import org.jpablo.typeexplorer.shared.models

case class DiagramOptions(
  showFields    : Boolean = false,
  showSignatures: Boolean = false,
  hiddenFields  : List[String] = DiagramOptions.hiddenFields,
  hiddenSymbols : List[models.Symbol] = DiagramOptions.hiddenSymbols
) derives JsonCodec

case class SymbolOptions(
  showFields: Boolean = false,
  showSignatures: Boolean = false
) derives JsonCodec

object DiagramOptions:

  private val hiddenFields = List(
    "canEqual",
    "copy",
    "equals",
    "hashCode",
    "productArity",
    "productElement",
    "productIterator",
    "productPrefix",
    "toString",
    "_1", "_2", "_3", "_4",
  )

  private val hiddenSymbols = List()

end DiagramOptions
