/*
 * Copyright 2022 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeHeaders, FakeRequest, Helpers}
import uk.gov.hmrc.checkeorinumber.connectors.EISConnector
import uk.gov.hmrc.checkeorinumber.models.{CheckMultipleEoriNumbersRequest, CheckResponse, EoriNumber}
import uk.gov.hmrc.checkeorinumber.utils.BaseSpec

import scala.concurrent.Future

class CheckEoriNumberControllerSpec extends BaseSpec with BeforeAndAfterEach {

  val eoriNumber: EoriNumber              = "GB123456789000"
  val invalidEoriNumber: EoriNumber       = "GB999999999999"
  val checkResponse: CheckResponse        = CheckResponse(eoriNumber, valid = true, None)
  val invalidCheckResponse: CheckResponse = CheckResponse(invalidEoriNumber, valid = false, None)
  val mockEISConnector: EISConnector      = mock[EISConnector]

  val controller = new CheckEoriNumberController(
    appConfig,
    Helpers.stubControllerComponents(),
    mockEISConnector
  )

  override def beforeEach(): Unit = {
    reset(mockEISConnector)
    super.beforeEach()
  }

  "GET /check-eori/:eoriNumber" should {
    "return 200 and expected valid-eori Json" in {
      when(mockEISConnector.checkEoriNumbers(any())(any(), any())).thenReturn(Future.successful(List(checkResponse)))

      val result = controller.check(eoriNumber)(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldEqual Json.toJson(List(checkResponse))
    }
    "return 404 and expected invalid-eori Json" in {
      when(mockEISConnector.checkEoriNumbers(any())(any(), any())).thenReturn(
        Future.successful(List(invalidCheckResponse))
      )
      val result = controller.check(invalidEoriNumber)(fakeRequest)
      status(result) shouldBe Status.NOT_FOUND
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
      when(mockEISConnector.checkEoriNumbers(any())(any(), any())).thenReturn(
        Future.successful(List(checkResponse, invalidCheckResponse))
      )
      val result: Future[play.api.mvc.Result] = controller.checkMultipleEoris().apply(request)
      status(result) shouldBe Status.OK
    }
    "return expected Json" in {
      when(mockEISConnector.checkEoriNumbers(any())(any(), any())).thenReturn(
        Future.successful(List(checkResponse, invalidCheckResponse))
      )
      val result: Future[play.api.mvc.Result] = controller.checkMultipleEoris().apply(request)
      contentAsJson(result) shouldEqual Json.toJson(
        List(checkResponse, invalidCheckResponse)
      )
    }
  }

}
