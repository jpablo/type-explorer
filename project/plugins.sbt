addSbtPlugin("io.spray"                    % "sbt-revolver"             % "0.9.1")
addSbtPlugin("com.eed3si9n"                % "sbt-assembly"             % "2.1.1")
addSbtPlugin("com.thesamet"                % "sbt-protoc"               % "1.0.6")
addSbtPlugin("org.portable-scala"          % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("org.scala-js"                % "sbt-scalajs"              % "1.14.0")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter"            % "1.0.0-beta40")
addSbtPlugin("com.github.sbt"              % "sbt-native-packager"      % "1.9.9")
addSbtPlugin("org.scalameta"               % "sbt-scalafmt"             % "2.4.6")
addSbtPlugin("com.eed3si9n"                % "sbt-buildinfo"            % "0.11.0")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.13"
addDependencyTreePlugin

ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
