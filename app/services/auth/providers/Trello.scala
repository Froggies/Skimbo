package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken

object Trello extends OAuthProvider {

  override val name = "trello"
  override val namespace = "tr"

  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val username = (me \ "username").asOpt[String]
      val name = (me \ "fullName").asOpt[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "gravatarHash").asOpt[String]
      val token = getToken(idUser).get
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(token.token, Some(token.secret))),
          username, 
          name, 
          description, 
          profileImage.map(img => "http://www.gravatar.com/avatar/"+img)))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details TRELLO")
        None
      }
    }
  }

}

