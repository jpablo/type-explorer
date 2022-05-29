# zio-http

```
[error] Modules were resolved with conflicting cross-version suffixes ...
[error]    org.scala-lang.modules:scala-collection-compat _3, _2.13
```

## solution
Remove scala-collection-compat when building for recent Scala versions

```
      `netty-incubator`,
//      `scala-compact-collection`,
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n <= 12 => Seq(`scala-compact-collection`)
        case _                       => Seq.empty
      }
    },

```
