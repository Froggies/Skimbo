package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import java.net.URLEncoder

object Scoopit extends OAuthProvider {

  override val name = "scoopit"
  override val namespace = "sc"

  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json \ "user"
      val id = (me \ "id").as[Int].toString
      val username = (me \ "shortName").asOpt[String]
      val name = (me \ "name").asOpt[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "avatarUrl").asOpt[String]
      val token = getToken(idUser).get
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(token.token, Some(token.secret))), 
          username, 
          name, 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details SCOOPIT")
        None
      }
    }
  }
  
}