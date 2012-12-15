package services.auth.actions

import scala.concurrent.Future

import models.user.ProviderUser
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.RequestHeader

trait AccountWsProvider extends WsProvider {

  protected val config: Configuration

  def getUserInfosUrl(): Option[String] = config.getString("urlUserInfos")

  /**
   * Retrieve user informations from provider
   */
  def getUser(implicit request: RequestHeader): Future[Option[ProviderUser]] = {
    config.getString("urlUserInfos").map(url =>
      fetch(url).get().map(response => distantUserToSkimboUser(request.session("id"), response))).getOrElse(Future { None })
  }

  /**
   * Transcript getUser to real User
   */
  def distantUserToSkimboUser(id: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = None

}