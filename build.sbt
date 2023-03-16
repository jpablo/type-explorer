import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version     = "3.3.0-RC3"
val scalametaVersion  = "4.7.5"
val zioHttpVersion    = "2.0.0-RC11"
val zioPreludeVersion = "1.0.0-RC16"
val zioVersion        = "2.0.5"

// TODO:
// - Maybe create a plugin to correctly set semanticdbTargetRoot for each subproject?
lazy val projectPath = settingKey[File]("projectPath")

Global / onChangedBuildSource := ReloadOnSourceChanges
Global / projectPath := (ThisBuild / baseDirectory).value / ".type-explorer/meta"
//Compile / semanticdbTargetRoot := projectPath.value
// ThisBuild / semanticdbEnabled := true
ThisBuild / resolvers += "Sonatype OSS Snapshots" at "https://s01.oss.sonatype.org/content/repositories/snapshots"
ThisBuild / organization := "net.jpablo"
ThisBuild / scalaVersion := scala3Version
ThisBuild / semanticdbVersion := scalametaVersion
ThisBuild / scalacOptions ++= // Scala 3.x options
  Seq(
    "-Ykind-projector:underscores",
    "-Ysafe-init",
    "-language:implicitConversions",
    "-source:future",
    "-deprecation"
//    "-Wunused:imports" // Not stable yet in 3.3.0-RC2
  )

ThisBuild / assemblyMergeStrategy := { _ => MergeStrategy.first }

lazy val protos =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("protos"))
    .settings(
      name         := "type-explorer-protos",
      version      := "0.1.0",
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
      scalacOptions ++= Seq("-Xsource:3"),
      Compile / PB.targets      := Seq(scalapb.gen(flatPackage = true) -> (Compile / sourceManaged).value / "scalapb"),
      Compile / PB.protoSources := Seq(file("protos/shared/src/main/protobuf")),
      Compile / semanticdbTargetRoot := projectPath.value
    )
    .jsSettings(
      scalaJSUseMainModuleInitializer := false
    )

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %%% "zio-prelude"       % zioPreludeVersion,
    "dev.zio" %%% "zio-json"          % "0.4.2",
    "dev.zio" %%% "zio-test"          % zioVersion % "test",
    "dev.zio" %%% "zio-test-sbt"      % zioVersion % "test",
    "dev.zio" %%% "zio-test-magnolia" % zioVersion % "test",
    "com.softwaremill.quicklens" %%% "quicklens" % "1.9.0",
    "org.scalameta"              %%% "scalameta" % scalametaVersion cross CrossVersion.for3Use2_13
  ),
  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
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
    .dependsOn(protos)
    .settings(
      name := "type-explorer-shared",
      version := "0.1.0",
      excludeDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-collection-compat",
        "org.scala-lang.modules" %% "scala-collection-compat_sjs1"
      ),
      Compile / semanticdbTargetRoot := projectPath.value
    )
    .settings(sharedSettings)
    .jsSettings(
      scalaJSUseMainModuleInitializer := false
    )

val mainBackendApp = "org.jpablo.typeexplorer.backend.webApp.WebApp"

lazy val backend =
  project
    .in(file("backend"))
    .dependsOn(shared.jvm, protos.jvm)
    .enablePlugins(JavaAppPackaging)
    .settings(
      name := "type-explorer-backend",
      version := "0.1.0",
      reStart / mainClass := Some(mainBackendApp),
      Compile / mainClass := Some(mainBackendApp),
      assembly / mainClass := Some(mainBackendApp),
      assembly/assemblyJarName := "type-explorer-uber-backend.jar",
      Compile / discoveredMainClasses := Seq(),
      libraryDependencies ++= Seq(
      "io.d11"                   %% "zhttp"             % zioHttpVersion,
      "dev.zio"                  %% "zio-logging"       % "2.1.9",
      "dev.zio"                  %% "zio-logging-slf4j" % "2.1.9",
      "dev.zio"                  %% "zio-logging-slf4j-bridge" % "2.1.9",
      "org.json4s"               %% "json4s-native"     % "4.0.6",
      "guru.nidi"                %  "graphviz-java"     % "0.18.1",
      "net.sourceforge.plantuml" %  "plantuml"          % "1.2022.14",
      "com.lihaoyi"              %% "scalatags"         % "0.11.1" cross CrossVersion.for3Use2_13, // Needed until org.scalameta-common upgrades to 3.x
       "io.github.arainko"       %% "ducktape"          % "0.1.3"
      ),
      excludeDependencies ++= Seq(
        "com.thesamet.scalapb"   %% "scalapb-runtime",
        "org.scala-lang.modules" %% "scala-collection-compat"
      ),
      Compile / semanticdbTargetRoot := projectPath.value
    )
    .settings(sharedSettings)

val publicDev = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")


lazy val ui =
  project
    .in(file("ui"))
    .dependsOn(shared.js, protos.js)
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      Compile / mainClass := Some("org.jpablo.typeexplorer.ui.app.MainJS"),
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
//          .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("org.jpablo.typeexplorer.ui")))
          .withSourceMap(true)
      },
      externalNpm := {
        //scala.sys.process.Process(List("npm", "install", "--silent", "--no-audit", "--no-fund"), baseDirectory.value).!
        baseDirectory.value / ".."
      },
      // Compile / npmDependencies ++= Seq(
      //   "@types/bootstrap" -> "5.2.2"
      // ),
      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "2.2.0",
        "com.raquo" %%% "laminar" % "0.14.5",
        "io.laminext" %%% "fetch" % "0.14.4"
      ),
      excludeDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-collection-compat_sjs1"
      ),
      publicDev := linkerOutputDirectory((Compile / fastLinkJS).value).getAbsolutePath,
      publicProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath,
      Compile / semanticdbTargetRoot := projectPath.value
    )
    .settings(sharedSettings)

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
      version := "0.1.0",
      welcomeMessage
    )


def welcomeMessage = onLoadMessage := {
  import scala.Console

  def header(text: String): String = s"${Console.RED}$text${Console.RESET}"

  def item(text: String): String = s"${Console.GREEN}> ${Console.CYAN}$text${Console.RESET}"

  def subItem(text: String): String = s"  ${Console.YELLOW}> ${Console.CYAN}$text${Console.RESET}"

  s"""|${header(s"Type Explorer ${version.value}")}
      |
      |Useful sbt tasks:
      |${item("~ backend/reStart")} - start backend server
      |${item("~ ui/fastLinkJS")} - compile ui
      """.stripMargin
}
