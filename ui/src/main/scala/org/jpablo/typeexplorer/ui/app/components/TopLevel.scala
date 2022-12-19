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
    inheritanceTabState <- AppState.inheritanceTabState
  yield
    div(
      cls := "flex flex-col h-full",
      AppHeader,
      TabsArea,
      AppFooter,
      // TODO: remove this in prod mode
      div(child.text <-- inheritanceTabState.$canvasSelection.signal.map(ds => s"canvasSelection: ${ds.size}")),
      div(child.text <-- inheritanceTabState.$activeSymbols.signal.map(ss => s"activeSymbols: ${ss.size}")),
    )
