package services.auth.actions

import play.api.mvc.RequestHeader
import scala.concurrent.Future
import models.ProviderUser
import play.api.Configuration
import play.api.Logger
import play.api.libs.concurrent.execution.defaultContext

trait AccountWsProvider extends WsProvider {

  protected val config: Configuration 
    
  /**
   * Retrieve user informations from provider
   */
  def getUser(implicit request: RequestHeader): Future[Option[ProviderUser]] = {
    config.getString("urlUserInfos").map { url => 
      Logger.info("fetch user infos on "+url)
      fetch(url).get().map{ response =>
          //Logger.info("Users infos : " + response.body)
          distantUserToSkimboUser(request.session("id"), response)
        }
    }.getOrElse(Future { None })
  }

  /**
   * Transcript getUser to real User
   */
  def distantUserToSkimboUser(id: String, response: play.api.libs.ws.Response): Option[ProviderUser] = None

}