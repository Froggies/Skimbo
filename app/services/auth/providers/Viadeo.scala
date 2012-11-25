package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken

object Viadeo extends OAuth2Provider {

  override val name = "viadeo"
  override val namespace = "vi"
  override val method = Post

  override def processToken(response: play.api.libs.ws.Response) = 
    Token((response.json \ "access_token").asOpt[String], None)
    
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val username = (me \ "nickname").asOpt[String]
      val name = (me \ "name").asOpt[String]
      val description = (me \ "interests").asOpt[String]
      val profileImage = (me \ "picture_medium").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, None)), 
          username, 
          name, 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details VIADEO")
        None
      }
    }
  }

}