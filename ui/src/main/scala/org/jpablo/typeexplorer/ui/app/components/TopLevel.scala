package org.jpablo.typeexplorer.ui.app.components

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.TabsArea
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTab.UserSelectionCommand
import org.jpablo.typeexplorer.ui.app.toggle
import zio.prelude.fx.ZPure

def TopLevel =
  for
    AppHeader <- AppHeader
    TabsArea  <- TabsArea
    $userSelectionCommand <- AppState.$userSelectionCommand
    inheritanceTabState <- AppState.inheritanceTabState
  yield
    div(
      cls := "flex flex-col h-full",
      AppHeader,
      TabsArea,
      AppFooter,
      $userSelectionCommand --> inheritanceTabState.$canvasSelection.updater[UserSelectionCommand] { (set, command) =>
        command match
          case UserSelectionCommand.SetTo(symbol) => Set(symbol)
          case UserSelectionCommand.Extend(symbol) => set + symbol
          case UserSelectionCommand.Clear => Set.empty

      },
      div(child.text <-- inheritanceTabState.$canvasSelection.signal.map(ds => s"canvasSelection: $ds")),
      div(child.text <-- inheritanceTabState.$activeSymbols.signal.map(ss => s"activeSymbols: ${ss.size}")),
    )
