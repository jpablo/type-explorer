import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.1.2"
val zioVersion = "2.0.0-RC6"
val zioJsonVersion = "0.3.0-RC8"
val circeVersion = "0.14.2"
val zioPreludeVersion = "1.0.0-RC14"
val scalametaVersion = "4.5.9"
val zioHttpVersion = "2.0.0-RC8+1-6d179026-SNAPSHOT"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
ThisBuild / organization := "net.jpablo"
ThisBuild / scalaVersion := scala3Version
ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions ++= // Scala 3.x options
  Seq(
//    "-Yrangepos",
    "-Ykind-projector:underscores",
    "-Ysafe-init",
    "-language:implicitConversions",
    "-source:future"
  )

lazy val protos =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("protos"))
    .settings(
      name := "type-explorer-protos",
      version := "0.1.0",
      scalaVersion := "2.13.6",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "common" % scalametaVersion % "protobuf",
        "org.scalameta" %%% "common" % scalametaVersion
      ),
      scalacOptions --= Seq(
        "-Ykind-projector:underscores",
        "-Ysafe-init",
        "-source:future"
      ),
      scalacOptions ++= Seq(
        "-Xsource:3"
      ),
      Compile / PB.targets := Seq(
        scalapb.gen(flatPackage = true) -> (Compile / sourceManaged).value / "scalapb"
      ),
      Compile / PB.protoSources  := Seq(
        file("protos/shared/src/main/protobuf")
      )
    )
    .jsSettings(
      scalaJSUseMainModuleInitializer := false
    )

/**
  * The configuration
  *   {{{ .crossType(CrossType.Pure).in(file("shared")) }}}
  *
  *   enables three subprojects:
  *   - shared/.js (js stuff)
  *   - shared/.jvm (jvm stuff)
  *   - shared/src (for shared code)
  *
  *   Check https://github.com/portable-scala/sbt-crossproject for more info
  */
lazy val shared =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("shared"))
    .settings(
      name := "type-explorer-shared",
      version := "0.1.0",
      libraryDependencies ++= Seq(
        "dev.zio" %%% "zio-prelude" % zioPreludeVersion
      )
    )
    .jsSettings(
      scalaJSUseMainModuleInitializer := false
    )

lazy val backend =
  project
    .in(file("backend"))
    .dependsOn(shared.jvm, protos.jvm)
    .settings(
      name := "type-explorer-backend",
      version := "0.1.0",
      libraryDependencies ++= Seq(
      "dev.zio"                  %% "zio"               % zioVersion,
      "dev.zio"                  %% "zio-json"          % zioJsonVersion,
      "dev.zio"                  %% "zio-test"          % zioVersion % "test",
      "dev.zio"                  %% "zio-test-sbt"      % zioVersion % "test",
      "dev.zio"                  %% "zio-test-magnolia" % zioVersion % "test",

      "io.d11"                   %% "zhttp"             % zioHttpVersion,
      "org.json4s"               %% "json4s-native"     % "4.0.5",

      "guru.nidi"                %  "graphviz-java"     % "0.18.1",
      "net.sourceforge.plantuml" %  "plantuml"          % "1.2022.5",

      "com.lihaoyi"              %% "scalatags"         % "0.11.1"         cross CrossVersion.for3Use2_13,
      "org.scalameta"            %% "scalameta"         % scalametaVersion cross CrossVersion.for3Use2_13,
      // "com.softwaremill.quicklens" %% "quicklens" % "1.7.5",
      ),
      excludeDependencies ++= Seq(
        "com.thesamet.scalapb"   % "scalapb-runtime_3",
        "org.scala-lang.modules" % "scala-collection-compat_3",
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

val publicDev = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")


lazy val ui =
  project
    .in(file("ui"))
    .dependsOn(shared.js, protos.js)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
          .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("testvite")))
      },

      externalNpm := {
        //scala.sys.process.Process(List("npm", "install", "--silent", "--no-audit", "--no-fund"), baseDirectory.value).!
        baseDirectory.value
      },

      libraryDependencies ++= Seq(
        "dev.zio"       %%% "zio"               % zioVersion,
        "dev.zio"       %%% "zio-test"          % zioVersion % "test",
        "dev.zio"       %%% "zio-test-sbt"      % zioVersion % "test",
        "dev.zio"       %%% "zio-test-magnolia" % zioVersion % "test",

        "org.scala-js"  %%% "scalajs-dom"       % "2.0.0",
        "com.raquo"     %%% "laminar"           % "0.14.2",
        "io.laminext"   %%% "fetch"             % "0.14.3",
        "io.laminext"   %%% "fetch-circe"       % "0.14.3",

        "org.scalameta" %%% "scalameta"         % scalametaVersion cross CrossVersion.for3Use2_13

//        "io.frontroute" %%% "frontroute" % "0.14.0"
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),

      publicDev := linkerOutputDirectory((Compile / fastLinkJS).value).getAbsolutePath,
      publicProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath

    )

def linkerOutputDirectory(v: Attributed[org.scalajs.linker.interface.Report]): File =
  v.get(scalaJSLinkerOutputDirectory.key).getOrElse {
    throw new MessageOnlyException("Linking report was not attributed with output directory. Please report this as a Scala.js bug.")
  }


lazy val root =
  project
    .in(file("."))
    .aggregate(protos.js, protos.jvm, backend, ui, shared.js, shared.jvm)
    .settings(
      name := "type-explorer",
      version := "0.1.0"
    )
