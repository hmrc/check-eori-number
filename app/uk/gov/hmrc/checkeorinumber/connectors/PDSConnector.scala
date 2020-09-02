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


import com.google.inject.Inject
import javax.inject.Singleton
import play.api.{Configuration, Environment}
import uk.gov.hmrc.checkeorinumber.models.{CheckResponse, EoriNumber}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PDSConnector @Inject()(
  http: HttpClient,
  environment: Environment,
  configuration: Configuration,
  servicesConfig: ServicesConfig
)
 {
   private val pdsURL = s"${servicesConfig.baseUrl("pds")}"

   //TODO update this to connect with PDS rather than DES
   private def desGet[O](url: String)(implicit rds: HttpReads[O], hc: HeaderCarrier, ec: ExecutionContext): Future[O] =
     http.GET[O](url)(rds, addHeaders, ec)

   private def addHeaders(implicit hc: HeaderCarrier): HeaderCarrier = {
     hc.withExtraHeaders(
       "Environment" -> servicesConfig.getConfString("des.environment", "")
     ).copy(authorization = Some(Authorization(s"Bearer ${servicesConfig.getConfString("des.token", "")}")))
   }

   def checkEoriNumber(
     eoriNumber: EoriNumber
   )(
     implicit hc: HeaderCarrier,
     ec: ExecutionContext
   ): Future[CheckResponse] = {
     //TODO update this to connect with PDS rather than DES
     val url = s"$pdsURL/check"

     http.GET[CheckResponse](url)
   }
}
