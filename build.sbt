import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.1.2"
val zioVersion = "2.0.0-RC6"
val zioJsonVersion = "0.3.0-RC8"
val circeVersion = "0.14.2"
val zioPreludeVersion = "1.0.0-RC14"
val scalametaVersion = "4.5.8"
val zioHttpVersion = "2.0.0-RC7+1-c29b7875+20220528-1913-SNAPSHOT"

ThisBuild / organization := "net.jpablo"
ThisBuild / scalaVersion := scala3Version
ThisBuild / scalacOptions ++=
  Seq(
//    "-Yrangepos",
    "-Ykind-projector:underscores",
    "-language:implicitConversions",
    "-source:future"
  )

/**
  * The configuration
  *   {{{ .crossType(CrossType.Pure).in(file("shared")) }}}
  *
  *   enable three subprojects:
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
        "io.circe" %%% "circe-core" % circeVersion,
        "io.circe" %%% "circe-generic" % circeVersion,
        "io.circe" %%% "circe-parser" % circeVersion
      )
    )
    .jsSettings(
      scalaJSUseMainModuleInitializer := false
    )

lazy val backend =
  project
    .in(file("backend"))
    .dependsOn(shared.jvm)
    .settings(
      name := "type-explorer-backend",
      version := "0.1.0",
      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"               % zioVersion,
//        "dev.zio" %% "zio-prelude"       % zioPreludeVersion,
        "dev.zio" %% "zio-test"          % zioVersion % "test",
        "dev.zio" %% "zio-test-sbt"      % zioVersion % "test",
        "dev.zio" %% "zio-test-magnolia" % zioVersion % "test",

        // This has a conflict with scalameta
        // org.scala-lang.modules:scala-collection-compat _3, _2.13
//        "dev.zio"  %% "zio-json" % zioJsonVersion,

        "io.d11"  %% "zhttp" % zioHttpVersion,

        "guru.nidi" % "graphviz-java" % "0.18.1",
        "net.sourceforge.plantuml" % "plantuml" % "1.2022.5",
        "com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.0" cross CrossVersion.for3Use2_13,
//        "com.softwaremill.quicklens" %% "quicklens" % "1.7.5",
//        "org.typelevel" %% "cats-core" % "2.6.1",

        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,

        "com.lihaoyi" %% "scalatags" % "0.11.1" cross CrossVersion.for3Use2_13,
        "org.scalameta" %% "scalameta" % scalametaVersion cross CrossVersion.for3Use2_13
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

val publicDev = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")


lazy val ui =
  project
    .in(file("ui"))
    .dependsOn(shared.js)
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
        "dev.zio" %%% "zio"               % zioVersion,
        "dev.zio" %%% "zio-prelude"       % zioPreludeVersion,
        "dev.zio" %%% "zio-test"          % zioVersion % "test",
        "dev.zio" %%% "zio-test-sbt"      % zioVersion % "test",
        "dev.zio" %%% "zio-test-magnolia" % zioVersion % "test",

        "com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.0" cross CrossVersion.for3Use2_13,
        "org.scalameta" %%% "scalameta" % scalametaVersion cross CrossVersion.for3Use2_13,
        "org.scala-js" %%% "scalajs-dom" % "2.0.0",
        "com.raquo" %%% "laminar" % "0.14.2",
        "io.laminext" %%% "fetch" % "0.14.3"
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
    .aggregate(backend, ui, shared.js, shared.jvm)
    .settings(
      name := "type-explorer",
      version := "0.1.0"
    )
