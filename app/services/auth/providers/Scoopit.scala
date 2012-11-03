package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.ProviderUser

object Scoopit extends OAuthProvider {

  override val name = "scoopit"
  override val namespace = "sc"
    
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json \ "user"
      val id = (me \ "id").as[Int].toString
      val username = (me \ "shortName").as[String]
      val name = (me \ "name").as[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "avatarUrl").asOpt[String]
      Some(ProviderUser(id, username, name, this.name, description, profileImage))
    } catch {
      case _ => {
        Logger.error("Error during fetching user details")
        None
      }
    }
  }
  
}