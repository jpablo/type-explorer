addSbtPlugin("io.spray"                    % "sbt-revolver"             % "0.9.1")
addSbtPlugin("com.thesamet"                % "sbt-protoc"               % "1.0.6")
addSbtPlugin("org.portable-scala"          % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("org.scala-js"                % "sbt-scalajs"              % "1.11.0")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter"            % "1.0.0-beta39")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.10"
addDependencyTreePlugin
