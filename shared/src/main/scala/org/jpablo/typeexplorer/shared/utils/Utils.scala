package org.jpablo.typeexplorer.shared.utils


extension [A, B] (f: A => B)
  inline def <|: (a: A): B = f(a)

