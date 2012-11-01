package services.auth

import providers._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws.WS.WSRequestHolder
import java.util.UUID
import models.User
import play.api.libs.concurrent.futureToPlayPromise
import play.api.Logger

trait GenericProvider extends Results {

  // Generic provider settings (override it)
  def name: String
  protected def namespace: String

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","

  // From config file
  protected lazy val config = current.configuration.getConfig("social." + name).get
  protected lazy val logo = config.getString("urlLogo").get

  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)

  /**
   * Execute authentification process with this provider and redirect to `redirectRoute`
   */
  def auth(redirectRoute: Call)(implicit request: RequestHeader): Result

  /**
   * Retrieve security token
   */
  def getToken(implicit request: RequestHeader): Option[Any]

  /**
   * Create a basic webservice call and sign the request with token
   */
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder

  /**
   * Assign unique ID to client after authentification
   */
  protected def generateUniqueId(session: Session) = {
    session + ("id" -> session.get("id").getOrElse(UUID.randomUUID().toString))
  }

  /**
   * Has the client a token on this service
   */
  def hasToken(implicit request: RequestHeader) = getToken.isDefined

  /**
   * Retrieve user informations from provider
   */
  def getUser(implicit request: RequestHeader): Option[User] = {
    config.getString("urlUserInfos").map { url => 
      fetch(url).get().await(20000).fold( //TODO : remove await quand tu pourras
        onError => {
          Logger.error("urlUserInfos timed out waiting for "+name)
          None
        },
        response => {
          Logger.info(name + " users infos : " + response.body)
          distantUserToSkimboUser(request.session("id"), response)
        })
    } getOrElse {
      Logger.error(name+" hasn't urlUserInfos in config !")
      None
    }
  }

  /**
   * Transcript getUser to real User
   */
  def distantUserToSkimboUser(id: String, response: play.api.libs.ws.Response): Option[User] = None

}