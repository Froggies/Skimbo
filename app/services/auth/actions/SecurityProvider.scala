package services.auth.actions

import play.api.mvc._
import java.util.UUID

trait SecurityProvider {

  /**
   * Execute authentification process with this provider and redirect to `redirectRoute`
   */
  def auth(redirectRoute: Call)(implicit request: RequestHeader): Result

  /**
   * Retrieve security token
   */
  def getToken(implicit request: RequestHeader): Option[Any]

 
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
  
}