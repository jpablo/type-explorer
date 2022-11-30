package org.jpablo.typeexplorer.ui.app.components

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.toggle
import zio.prelude.fx.ZPure

def TopLevel =
  for
    AppHeader <- AppHeader
    TabsArea  <- TabsArea
    $svgSymbolSelected <- AppState.svgSymbolSelected
    inheritanceTabState <- AppState.inheritanceTabState
  yield
    div(
      idAttr := "te-toplevel",
      AppHeader,
      TabsArea,
      AppFooter,
      $svgSymbolSelected --> inheritanceTabState.$canvasSelection.updater[models.Symbol](_ `toggle` _),
      div(child.text <-- inheritanceTabState.$canvasSelection.signal.map(ds => s"canvasSelection: $ds")),
      div(child.text <-- inheritanceTabState.$activeSymbols.signal.map(ss => s"activeSymbols: $ss")),
    )
