import play.core.PlayVersion.current
import sbt._

object AppDependencies {

  val bootstrapVersion = "10.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30" % bootstrapVersion ,
    "org.scalatest"          %% "scalatest"              % "3.2.19"         ,
    "org.playframework"      %% "play-test"              % current          ,
    "org.scalatestplus.play" %% "scalatestplus-play"     % "7.0.2"
  ).map(_ % Test)
}
