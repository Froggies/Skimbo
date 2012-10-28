package services.auth

import providers._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws.WS.WSRequestHolder
import java.util.UUID

/*object GenericProvider {

  val providers:Seq[GenericProvider] = Seq(Twitter, GitHub, Facebook, 
      GooglePlus, LinkedIn, StackExchange, Trello, Viadeo, Scoopit)

  def hasToken(implicit request: RequestHeader):Boolean = {
    for (provider <- providers) {
      if(provider.hasToken) {
        return true
      }
    }
    return false
  }
}*/

trait GenericProvider extends Results {

  // Generic provider settings (override it)
  def name: String
  protected def namespace: String

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","

  // From config file
  protected lazy val config  = current.configuration.getConfig("social."+name).get
  protected lazy val logo = config.getString("urlLogo").get

  // Common config
  lazy val authRoute: Call  = controllers.routes.Application.authenticate(name)

  // Generic operations
  def hasToken(implicit request: RequestHeader) = {
    getToken match {
      case Some(credentials) => true
      case _ => false
    }
  }

  // Basic operations on providers
  // TODO : Add redirectOk / redirectError
  def auth(redirectRoute: Call)(implicit request: RequestHeader): Result
  def getToken(implicit request: RequestHeader): Any //FIXME : Virer ça !
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder
  
  // Attache unique ID to client after authentification
  protected def generateUniqueId(session : Session) = {
    session + ("id" -> session.get("id").getOrElse(UUID.randomUUID().toString))
  }

}