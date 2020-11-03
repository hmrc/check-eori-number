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

package uk.gov.hmrc.checkeorinumber.controllers

import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers, FakeHeaders}
import uk.gov.hmrc.checkeorinumber.utils.BaseSpec
import uk.gov.hmrc.checkeorinumber.connectors.EISConnector
import uk.gov.hmrc.checkeorinumber.models.{CheckMultipleEoriNumbersRequest, EoriNumber, CheckResponse}
import uk.gov.hmrc.checkeorinumber.models.internal.{PartyResponse, IdentificationsResponse}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class CheckEoriNumberControllerSpec extends BaseSpec {

  val eoriNumber: EoriNumber = "GB123456789000"
  val invalidEoriNumber: EoriNumber = "GB999999999999"
  val checkResponse = CheckResponse(eoriNumber, true, None, None)
  val invalidCheckResponse = CheckResponse(invalidEoriNumber, false, None, None)

  val controller = new CheckEoriNumberController(
    appConfig,
    Helpers.stubControllerComponents(),
    new MockEISConnector()
  )

  "GET /check-eori/:eoriNumber" should {
    "return 200" in {
      val result = controller.check(eoriNumber)(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldEqual Json.toJson(List(checkResponse))
    }
    "return expected valid-eori Json" in {
      val result = controller.check(eoriNumber)(fakeRequest)
      contentAsJson(result) shouldEqual Json.toJson(List(checkResponse))
    }
    "return expected invalid-eori Json" in {
      val result = controller.check(invalidEoriNumber)(fakeRequest)
      contentAsJson(result) shouldEqual Json.toJson(List(invalidCheckResponse))
    }
  }

  "POST /check-multiple-eori" should {
    val jsonBody = Json.toJson(
      CheckMultipleEoriNumbersRequest(
        List(eoriNumber, invalidEoriNumber)
      )
    )
    val request = FakeRequest("POST", "/check-multiple-eori", FakeHeaders(), jsonBody)
    "return 200" in {
      val result: Future[play.api.mvc.Result] = controller.checkMultipleEoris().apply(request)
      status(result) shouldBe Status.OK
    }
    "return expected Json" in {
      val result: Future[play.api.mvc.Result] = controller.checkMultipleEoris().apply(request)
      contentAsJson(result) shouldEqual Json.toJson(
        List(checkResponse, invalidCheckResponse)
      )
    }
  }

  class MockEISConnector extends EISConnector {

    val mockPartyResponse =
      PartyResponse(List(IdentificationsResponse(checkResponse)))

    val mockPartyResponseInvalid =
      PartyResponse(List(IdentificationsResponse(invalidCheckResponse)))

    def checkEoriNumbers(
      check: CheckMultipleEoriNumbersRequest
    )(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext
    ): Future[PartyResponse] = check.eoriNumbers match {
      case List(`eoriNumber`) => Future.successful(mockPartyResponse)
      case List(`eoriNumber`,`invalidEoriNumber`) => Future.successful(
        PartyResponse(
          List(
            IdentificationsResponse(checkResponse),
            IdentificationsResponse(invalidCheckResponse)
          )
        )
      )
      case _=> Future.successful(mockPartyResponseInvalid)
    }
  }

}
