package org.jpablo.typeexplorer.util

import scala.annotation.targetName

object Operators:

  extension [T] (t: T)
    @targetName("pipe")
    def |>[U](f: T => U): U = f(t)


