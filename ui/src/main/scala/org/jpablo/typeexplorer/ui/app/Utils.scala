package org.jpablo.typeexplorer.ui.app



extension [A] (set: Set[A])
  def toggle(a: A) =
    set.toggleWith(a, !set.contains(a))

  def toggleWith(a: A, b: Boolean) =
    if b then set + a else set - a


extension [K] (map: Map[K, Boolean])
  def toggle(k: K, initial: Boolean = false) =
    map + (k -> !map.getOrElse(k, initial))
