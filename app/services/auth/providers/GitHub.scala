package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser

object GitHub extends OAuth2Provider {

  override val name = "github"
  override val namespace = "gh"
  override val method = Post
  override val accessTokenHeaders = Seq("Accept" -> "application/json")

  override def processToken(response: play.api.libs.ws.Response) =
    Token((response.json \ "access_token").asOpt[String], None)

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[Int].toString
      val username = (me \ "login").asOpt[String]
      val name = (me \ "name").asOpt[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "avatar_url").asOpt[String]
      Some(ProviderUser(id, this.name, username, name, description, profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details GITHUB")
        None
      }
    }
  }
    
}