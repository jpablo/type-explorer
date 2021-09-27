val scala3Version = "3.0.2"
val zioVersion = "1.0.11"
val circeVersion = "0.14.1"

ThisBuild / organization := "net.jpablo"


lazy val root =
  project
    .in(file("."))
    .settings(
      name := "type-explorer",
      version := "0.1.0",
    )

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "type-explorer-core",
      version := "0.1.0",

      scalaVersion := scala3Version,

      libraryDependencies ++= Seq(
        "dev.zio" %% "zio"               % zioVersion,
        "dev.zio" %% "zio-prelude"       % "1.0.0-RC6",
        "dev.zio" %% "zio-test"          % zioVersion % "test",
        "dev.zio" %% "zio-test-sbt"      % zioVersion % "test",
        "dev.zio" %% "zio-test-magnolia" % zioVersion % "test",

        "com.softwaremill.quicklens" %% "quicklens" % "1.7.4",
        "org.typelevel" %% "cats-core" % "2.6.1",

        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    )


scalacOptions ++= Seq(
  "-Ykind-projector:underscores",
  "-language:implicitConversions"
)
