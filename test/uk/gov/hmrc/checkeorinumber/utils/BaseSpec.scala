/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.checkeorinumber.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.test.{FakeRequest}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.checkeorinumber.config.AppConfig
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait BaseSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val fakeRequest               = FakeRequest("GET", "/")
  val env                       = Environment.simple()
  val configuration             = Configuration.load(env)
  val serviceConfig             = new ServicesConfig(configuration)
  val appConfig                 = new AppConfig(configuration, serviceConfig)
  implicit val executionContext = app.injector.instanceOf[ExecutionContext]
  implicit val headerCarrier    = HeaderCarrier()

}