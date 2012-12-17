package services.auth.providers

import play.api._
import play.api.mvc._
import models.user.ProviderUser
import services.auth._
import play.api.libs.ws.Response
import models.user.SkimboToken

object Twitter extends OAuthProvider {

  override val name = "twitter"
  override val namespace = "tw"

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[Int].toString
      val username = (me \ "screen_name").asOpt[String]
      val name = (me \ "name").asOpt[String]
      val description = (me \ "description").asOpt[String]
      val profileImage = (me \ "profile_image_url").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, Some(getToken.get.secret))), 
          username, 
          name, 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details TWITTER (certainly Rate Limit Exceeded)")
        Logger.error(response.json.toString)
        None
      }
    }
  }
  
  override def isInvalidToken(response: Response): Boolean = response.status != 429
  
  override def isRateLimiteError(response: Response): Boolean = response.status == 429

}

