package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.Owner
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, PackagesOptions, Path}

/** Convenience wrapper around a Var[Project]
  */
case class ProjectVar(project: Var[Project])(using Owner):

  export project.{signal, update, updater}

  val basePaths: Signal[List[Path]] =
    project.signal.map(_.projectSettings.basePaths)

  val name = project.zoom(_.name)((p, n) => p.copy(name = n))

  val packagesOptions: Signal[PackagesOptions] =
    project.signal.map(_.packagesOptions)

  val diagramOptions: Signal[Vector[DiagramOptions]] =
    project.signal.map(_.pages.map(_.diagramOptions))

  val advancedMode: Signal[Boolean] =
    project.signal.map(_.advancedMode)

  def page(i: Int): Var[Page] =
    project.zoom(_.pages(i))((p, page) => p.modify(_.pages.at(i)).setTo(page))

end ProjectVar
