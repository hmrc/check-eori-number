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

import com.google.inject.{ImplementedBy, Inject}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import play.api.libs.json._
import uk.gov.hmrc.checkeorinumber.models._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[EISConnectorImpl])
trait EISConnector {

  def checkEoriNumbers(
    checkRequest: CheckMultipleEoriNumbersRequest
  )(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[List[CheckResponse]]

}

trait EISJsonConverter {

  val logger: Logger = Logger(getClass)
  implicit val checkResponseReads = new Reads[CheckResponse] {

    override def reads(json: JsValue): JsResult[CheckResponse] = {
      val basePath = json \ "identifications"
      val eori = (basePath \ "eori").as[String]

      JsSuccess(
        CheckResponse(
          eori,
          (basePath \ "valid").as[Boolean],
          (
            (basePath \ "traderName").asOpt[TraderName],
            (basePath \ "address").asOpt[Address],
            (basePath \ "address").asOpt[AddressNoPostCode]
          ) match {
            case (Some(_), None, None) =>
              logger.warn(s"traderName found but address is empty for [$eori]")
              None
            case (None, _ ,Some(_))=>
              logger.warn(s"address found but traderName is empty for [$eori]")
              None
            case (Some(a), Some(b), _) =>
              Some(CompanyDetails(a, b))
            case (Some(a),None, Some(b)) =>
              Some(CompanyDetails(a, Address(b.streetAndNumber, b.cityName, "")))
            case (None, None, None) => None
          }
        )
      )

    }
  }

}

@Singleton
class EISConnectorImpl @Inject()(
  http: HttpClient,
  servicesConfig: ServicesConfig
) extends EISConnector with EISJsonConverter {

   private val eisURL = s"${servicesConfig.baseUrl("eis")}"

   private def addHeaders(implicit hc: HeaderCarrier): HeaderCarrier = {

     //HTTP-date format defined by RFC 7231 e.g. Fri, 01 Aug 2020 15:51:38 GMT+1
     val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O")

     hc.withExtraHeaders(
       "X-Forwarded-Host" -> "mdtp",
       "X-Correlation-ID" -> UUID.randomUUID().toString,
       HeaderNames.DATE -> ZonedDateTime.now().format(formatter),
       HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
       HeaderNames.ACCEPT -> ContentTypes.JSON,
       "Environment" -> s"${servicesConfig.getConfString("eis.environment", "")}"
     ).copy(
       authorization = None
     )
   }

   def checkEoriNumbers(
     checkRequest: CheckMultipleEoriNumbersRequest
   )(
     implicit hc: HeaderCarrier,
     ec: ExecutionContext
   ): Future[List[CheckResponse]] = {

     val url = s"$eisURL/gbeorichecker/gbeorirequest/v1"
     val json = http.POST[CheckMultipleEoriNumbersRequest, JsObject](
       url,
       checkRequest,
       Seq("Authorization" -> s"Bearer ${servicesConfig.getConfString("eis.token", "")}")
     )(implicitly, implicitly, addHeaders, ec)

     json.map(x => (x \ "party").as[List[CheckResponse]])
   }
}
