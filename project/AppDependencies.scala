import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val bootstrapVersion = "6.4.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "uk.gov.hmrc"             %% "simple-reactivemongo"       % "8.1.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % bootstrapVersion                 % Test,
    "org.scalatest"           %% "scalatest"                % "3.2.12"                 % Test,
    "com.typesafe.play"       %% "play-test"                % current                 % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"             % "0.64.0"               % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "5.1.0"                 % "test, it"
  )
}
