package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.Owner
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, PackagesOptions, Path, ProjectSettings}

/** Convenience wrapper around a Var[Project]
  */
case class ActiveProject(project: PersistentVar[Project])(using Owner):

  export project.{signal, update, updater}

  val basePaths: Signal[List[Path]] =
    project.signal.map(_.projectSettings.basePaths)

  val name: Var[String] =
    project.zoom(_.name)((p, n) => p.copy(name = n))

  val packagesOptions: Signal[PackagesOptions] =
    project.signal.map(_.packagesOptions)

  val projectSettings: Signal[ProjectSettings] =
    project.signal.map(_.projectSettings)

  val diagramOptions: Signal[Vector[DiagramOptions]] =
    project.signal.map(_.pages.map(_.diagramOptions))

  val advancedMode: Signal[Boolean] =
    project.signal.map(_.advancedMode)

  def pageV(i: Int): Var[Page] =
    project.zoom(_.pages(i))((p, page) => p.modify(_.pages.at(i)).setTo(page))

  val pages: Signal[Vector[Page]] =
    project.signal.map(_.pages)

  def newPage(): Unit =
    project.update(_.modify(_.pages).using(_ :+ Page()))

end ActiveProject
