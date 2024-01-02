package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.Owner
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, PackagesOptions, Path, ProjectSettings}

/** Convenience wrapper around a Var[Project]
  */
case class ActiveProject(project: PersistentVar[Project])(using o: Owner):

  //  export project.{signal, update, updater}
  val signal = project.signal
  val update = project.update
  val updater = project.updater

  val basePaths: Signal[List[Path]] =
    project.signal.map(_.projectSettings.basePaths).distinct

  val name: Var[String] =
    project.zoom(_.name)((p, n) => p.copy(name = n))

  val packagesOptions: Signal[PackagesOptions] =
    project.signal.map(_.packagesOptions).distinct

  val projectSettings: Signal[ProjectSettings] =
    project.signal.map(_.projectSettings).distinct

  val diagramOptions: Signal[Vector[DiagramOptions]] =
    project.signal.map(_.pages.map(_.diagramOptions)).distinct

  val advancedMode: Signal[Boolean] =
    project.signal.map(_.advancedMode).distinct

  def pageV(pageId: String): Var[Page] =
    project.zoom { p =>
      p.pages.find(_.id == pageId).getOrElse(p.pages.last)
    }((p, page) => p.modify(_.pages.eachWhere(_.id == pageId)).setTo(page))

  val pages: Signal[Vector[Page]] =
    project.signal.map(_.pages).distinct

  def newPage(): Unit = {
    val page: Page = Page()
    project.update { p =>
      p.modify(_.pages)
        .using(_ :+ page)
        .modify(_.activePage)
        .using(_ => p.pages.length)
    }
  }

  def closePage(i: Int): Unit =
    project.update(_.modify(_.pages).using(_.patch(i, Nil, 1)))

  def closeActivePage(): Unit =
    project.update: p =>
      p.modify(_.pages).using(_.patch(p.activePage, Nil, 1))

  def setActivePage(id: String): Unit =
    project.update(_.setActivePageId(id))

  def getActivePageId: Signal[String] =
    project.signal.map(_.activePageId)

end ActiveProject
