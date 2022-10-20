package org.jpablo.typeexplorer.ui.app.components

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsAreaZ
import org.jpablo.typeexplorer.ui.app.toggle
import zio.prelude.fx.ZPure

def TopLevel =
  for
    AppHeader <- AppHeaderZ
    TabsArea <- TabsAreaZ
    $diagramSelection <- AppState.$diagramSelection
    $setSymbol <- ZPure.service[Unit, EventBus[Symbol]]
  yield
    val updateSymbolSelection = $diagramSelection.updater[Symbol](_ `toggle` _)
    div(
      idAttr := "te-toplevel",
      $setSymbol --> updateSymbolSelection,
      AppHeader,
      TabsArea,
      AppFooter,
      div(child.text <-- $diagramSelection.signal.map(_.toString)),
    )
