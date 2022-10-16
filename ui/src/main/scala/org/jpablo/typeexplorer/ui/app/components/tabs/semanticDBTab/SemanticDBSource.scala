package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}

import com.raquo.airstream.core.EventStream

def SourceCodeTab($sourceCode: EventStream[String], position: Option[SymbolOccurrence] = None) =
  div(
    pre(
      child.text <-- $sourceCode
    )
  )
