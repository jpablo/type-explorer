addSbtPlugin("io.spray"                    % "sbt-revolver"             % "0.9.1")
addSbtPlugin("com.thesamet"                % "sbt-protoc"               % "1.0.6")
addSbtPlugin("org.portable-scala"          % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("org.scala-js"                % "sbt-scalajs"              % "1.13.0")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter"            % "1.0.0-beta40")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
addDependencyTreePlugin

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

