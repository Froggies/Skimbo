package services.auth.providers

import play.api._
import play.api.mvc._
import models._
import services.auth._
import play.api.libs.ws.Response

object Twitter extends OAuthProvider {

  override val name = "twitter"
  override val namespace = "tw"

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response): Option[User] = {
    try {
      val me = response.json
      val id = (me \ "id").as[Int]
      val username = (me \ "screen_name").as[String]
      val name = (me \ "name").as[String]
      val description = (me \ "description").asOpt[String]
      val profileImage = (me \ "profile_image_url").asOpt[String]
      Some(User(ident, username, name, this.name, description, profileImage))
    } catch {
      case _ => {
        Logger.error("Error during fetching user details")
        None
      }
    }
  }

}

