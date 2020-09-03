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

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.checkeorinumber.config.AppConfig
import uk.gov.hmrc.checkeorinumber.connectors.PDSConnector
import uk.gov.hmrc.checkeorinumber.models.{Address, CheckResponse, EoriNumber, EoriRegisteredCompany}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.{ExecutionContext, Future}

class CheckEoriNumberController @Inject()(
  appConfig: AppConfig,
  cc: ControllerComponents,
  connector: PDSConnector
) extends BackendController(cc) {

  implicit val ec: ExecutionContext = cc.executionContext

  def check(eoriNumber: EoriNumber): Action[AnyContent] =
  //TODO Implement this with stub when we recieve PDS data structure
  //    Action.async { implicit request =>
  //      connector.checkEoriNumber(eoriNumber).map { c =>
  //        Ok(Json.toJson(c))
  //    }
  //  }
    Action.async { implicit request =>
      eoriNumber match {
        case en if en.last == '1' =>
          Future.successful(
            Ok(
              Json.toJson(
                CheckResponse(
                  isValidEori = true,
                  en,
                  Some(
                    EoriRegisteredCompany(
                      "Hay's Limonard",
                      Address(
                        "House 1",
                        Some("Street 2"),
                        Some("Town 3"),
                        Some("County 4"),
                        None,
                        Some("AA111AA"),
                        "GB"
                      )
                    )
                  )
                )
              )
            )
          )
        case en if en.last == '2' =>
          Future.successful(
            Ok(
              Json.toJson(CheckResponse(isValidEori = true, en, None))
            )
          )
        case en => Future.successful(Ok(Json.toJson(CheckResponse(isValidEori = false, en, None))))
      }
    }
}
