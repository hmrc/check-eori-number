/*
 * Copyright 2024 HM Revenue & Customs
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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.OptionValues

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.checkeorinumber.connectors.{EISConnector, EISConnectorImpl}
import uk.gov.hmrc.checkeorinumber.models.{CheckMultipleEoriNumbersRequest, CheckResponse, EoriNumber}
import uk.gov.hmrc.http.HeaderCarrier
import utils.WiremockServer

import java.time.{ZoneId, ZonedDateTime}

class EISConnectorISpec
    extends AnyFreeSpec
    with WiremockServer
    with ScalaFutures
    with OptionValues
    with IntegrationPatience {

  "EISConnector" - {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    lazy val app: Application = new GuiceApplicationBuilder()
      .configure(
        Map("microservice.services.eis.port" -> mockServer.port(), "microservice.services.eis.environment" -> "IT")
      )
      .build()

    lazy val connector = app.injector.instanceOf[EISConnectorImpl]

    "EISConnector" - {

      "return eori response" in {

        val eoriNumber: EoriNumber = "GB025115110987654"
        val processingDate         = ZonedDateTime.now.withZoneSameInstant(ZoneId.of("Europe/London"))
        val expectedCheckResponse = CheckResponse(
          eoriNumber,
          valid = true,
          None,
          processingDate
        )

        val jsonResponse = Json.toJson(List(expectedCheckResponse))
        postStub(jsonResponse, OK)

        val response = connector.checkEoriNumbers(CheckMultipleEoriNumbersRequest(List(eoriNumber)))
        whenReady(response) { res =>
          res.head mustEqual expectedCheckResponse
        }
      }
    }
  }

  private def postStub(body: JsValue, status: Int) =
    mockServer.stubFor(
      post(urlMatching("/gbeorichecker/gbeorirequest/v1"))
        .willReturn(
          aResponse()
            .withStatus(status)
            .withBody(body.toString())
        )
    )

}
