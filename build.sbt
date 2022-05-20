import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.1.2"
val zioVersion = "2.0.0-M6-2"
val circeVersion = "0.14.1"
val zioPreludeVersion = "1.0.0-RC7"

ThisBuild / organization := "net.jpablo"


lazy val shared =
  project
    .in(file("shared"))
    .settings(
      name := "type-explorer-shared",
      version := "0.1.0",
      scalaVersion := scala3Version
    )

lazy val core =
  project
    .in(file("core"))
    .dependsOn(shared)
    .settings(
      name := "type-explorer-core",
      version := "0.1.0",

      scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"               % zioVersion,
        "dev.zio" %% "zio-prelude"       % "1.0.0-RC8",
        "dev.zio" %% "zio-test"          % zioVersion % "test",
        "dev.zio" %% "zio-test-sbt"      % zioVersion % "test",
        "dev.zio" %% "zio-test-magnolia" % zioVersion % "test",

        ("com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.0").cross(CrossVersion.for3Use2_13),
//        "com.softwaremill.quicklens" %% "quicklens" % "1.7.5",
//        "org.typelevel" %% "cats-core" % "2.6.1",

//        "io.circe" %% "circe-core" % circeVersion,
//        "io.circe" %% "circe-generic" % circeVersion,
//        "io.circe" %% "circe-parser" % circeVersion,

        ("org.scalameta" %% "scalameta" % "4.4.30").cross(CrossVersion.for3Use2_13),
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )

val publicDev = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")


lazy val ui =
  project
    .in(file("ui"))
    .dependsOn(shared)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      scalaVersion := scala3Version,
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

        ("com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.0").cross(CrossVersion.for3Use2_13),
        ("org.scalameta" %%% "scalameta" % "4.4.30").cross(CrossVersion.for3Use2_13),
        "org.scala-js" %%% "scalajs-dom" % "2.0.0",
        "com.raquo" %%% "laminar" % "0.14.2",
//        "io.frontroute" %%% "frontroute" % "0.14.0"
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),

      publicDev := linkerOutputDirectory((Compile / fastLinkJS).value).getAbsolutePath(),
      publicProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath(),

    )

def linkerOutputDirectory(v: Attributed[org.scalajs.linker.interface.Report]): File =
  v.get(scalaJSLinkerOutputDirectory.key).getOrElse {
    throw new MessageOnlyException("Linking report was not attributed with output directory. Please report this as a Scala.js bug.")
  }


lazy val root =
  project
    .in(file("."))
    .aggregate(core, ui)
    .settings(
      name := "type-explorer",
      version := "0.1.0",
    )



scalacOptions ++= Seq(
  "-Ykind-projector:underscores",
  "-language:implicitConversions"
)
