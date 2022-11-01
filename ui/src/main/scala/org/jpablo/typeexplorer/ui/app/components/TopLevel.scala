package org.jpablo.typeexplorer.ui.app.components

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.toggle
import zio.prelude.fx.ZPure

def TopLevel =
  for
    AppHeader <- AppHeader
    TabsArea  <- TabsArea
    $diagramSelection <- AppState.$diagramSelection
    $selectedNamespace <- AppState.$selectedNamespace
  yield
    div(
      idAttr := "te-toplevel",
      AppHeader,
      TabsArea,
      AppFooter,
      $selectedNamespace --> $diagramSelection.updater[Symbol](_ `toggle` _),
      div(child.text <-- $diagramSelection.signal.map(_.toString)),
    )
