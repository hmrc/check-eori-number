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

package uk.gov.hmrc.checkeorinumber.connectors

import play.api.libs.json._
import uk.gov.hmrc.checkeorinumber.models.CheckResponse
import uk.gov.hmrc.checkeorinumber.utils.BaseSpec

class EISConnectorSpec extends BaseSpec with EISJsonConverter {

  val eisJson = Json.parse("""
      |{
      |    "party": [
      |        {
      |          "identifications": {
      |            "eori": "GB025115110987654",
      |            "valid": true,
      |            "traderName": "Firstname LastName",
      |            "address": {
      |              "streetAndNumber": "999 High Street",
      |              "cityName": "CityName",
      |              "postcode": "SS99 1AA"
      |            }
      |          }
      |        },
      |        {
      |          "identifications": {
      |            "eori": "GB025115110987650",
      |            "valid": true,
      |            "traderName": "Firstname LastName"
      |          }
      |        },
      |        {
      |          "identifications": {
      |            "eori": "GB025115166368546",
      |            "valid": true,
      |            "address": {
      |              "streetAndNumber": "999 High Street",
      |              "cityName": "CityName",
      |              "postcode": "SS99 1AA"
      |            }
      |          }
      |        },
      |        {
      |          "identifications": {
      |            "eori": "GB025115166361111",
      |            "valid": false
      |          }
      |        }
      |      ]
      |}
    """.stripMargin).as[JsObject]

  "conversion of EIS json" should {
    val checkResponseList = (eisJson \ "party").as[List[CheckResponse]]
    "return a List[CheckReponse]" in {
      checkResponseList.isInstanceOf[List[CheckResponse]] shouldBe true
    }
    "include companyDetails only if both traderName and address are present" in {
      checkResponseList(0).companyDetails.isDefined shouldBe true
      checkResponseList(1).companyDetails.isEmpty shouldBe true
      checkResponseList(2).companyDetails.isEmpty shouldBe true
    }
  }

}
