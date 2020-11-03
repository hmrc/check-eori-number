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

package uk.gov.hmrc.checkeorinumber.connectors


import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.google.inject.{Inject, ImplementedBy}
import javax.inject.Singleton
import java.util.UUID

import play.api.{Configuration, Environment}
import play.api.http.{ContentTypes, HeaderNames}
import uk.gov.hmrc.checkeorinumber.models.CheckMultipleEoriNumbersRequest
import uk.gov.hmrc.checkeorinumber.models.internal.PartyResponse
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[EISConnectorImpl])
trait EISConnector {

  def checkEoriNumbers(
    checkRequest: CheckMultipleEoriNumbersRequest
  )(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[PartyResponse]

}

@Singleton
class EISConnectorImpl @Inject()(
  http: HttpClient,
  environment: Environment,
  configuration: Configuration,
  servicesConfig: ServicesConfig
) extends EISConnector {

   private val eisURL = s"${servicesConfig.baseUrl("eis")}"

   private def addHeaders(implicit hc: HeaderCarrier): HeaderCarrier = {

     //HTTP-date format defined by RFC 7231 e.g. Fri, 01 Aug 2020 15:51:38 GMT+1
     val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O")

     hc.withExtraHeaders(
       "X-Forwarded-Host" -> "mdtp",
       "X-Correlation-ID" -> UUID.randomUUID().toString,
       HeaderNames.DATE -> ZonedDateTime.now().format(formatter),
       HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
       HeaderNames.ACCEPT -> ContentTypes.JSON
     ).copy(
       authorization = Some(Authorization(s"Bearer ${servicesConfig.getConfString("eoi.token", "")}"))
     )
   }

   def checkEoriNumbers(
     checkRequest: CheckMultipleEoriNumbersRequest
   )(
     implicit hc: HeaderCarrier,
     ec: ExecutionContext
   ): Future[PartyResponse] = {
     val url = s"$eisURL/gbeorichecker/gbeorirequest/v1"
     http.POST[CheckMultipleEoriNumbersRequest, PartyResponse](url, checkRequest)(implicitly, implicitly, addHeaders, ec)
   }
}
