import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.13"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s",
    scoverageSettings
  )
  .settings(resolvers += Resolver.jcenterRepo)

scalafmtOnCompile        := true
PlayKeys.playDefaultPort := 8351
val appName = "check-eori-number"

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.test)

lazy val scoverageSettings: Seq[Setting[_]] = Seq(
  coverageExcludedPackages := "<empty>;Reverse.*;.*config.ErrorHandler;.*components.*;" +
    ".*javascript.*;.*Routes.*;.*viewmodels.*;.*ViewUtils.*;.*GuiceInjector;.*views.*;" +
    ".*Routes.*;.*viewmodels.govuk.*;app.*;prod.*",
  coverageMinimumStmtTotal := 90,
  coverageFailOnMinimum    := false,
  coverageHighlighting     := true,
  Test / parallelExecution := false
)
