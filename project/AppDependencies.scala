import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "8.4.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-29" % bootstrapVersion
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-29" % bootstrapVersion % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.15"         % Test,
    "com.typesafe.play"      %% "play-test"              % current          % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.64.8"         % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "6.0.0"          % "test, it"
  )
}
