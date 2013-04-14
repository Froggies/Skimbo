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
  def getUser(idUser: String): Future[Option[ProviderUser]] = {
    config.getString("urlUserInfos").map(url =>
      fetch(idUser, url).get().map(response => distantUserToSkimboUser(idUser, response))).getOrElse(Future { None })
  }

  /**
   * Transcript getUser to real User
   */
  def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = None

}