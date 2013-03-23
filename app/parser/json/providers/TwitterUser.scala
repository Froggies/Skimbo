package parser.json.providers

import play.api.Logger
import play.api.mvc.RequestHeader
import services.auth.providers.Twitter
import models.user.SkimboToken
import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue

object TwitterUser extends GenericJsonParserUser {

  override def asProviderUser(json: JsValue)(implicit request: RequestHeader): Option[models.user.ProviderUser] = {
      val id = (json \ "id").as[Int].toString
      val username = (json \ "screen_name").asOpt[String]
      val name = (json \ "name").asOpt[String]
      val description = (json \ "description").asOpt[String]
      val profileImage = (json \ "profile_image_url").asOpt[String]
      val token = Twitter.getToken
      Some(models.user.ProviderUser(
          id, 
          Twitter.name, 
          Some(SkimboToken(token.get.token, Some(token.get.secret))), 
          username, 
          name, 
          description, 
          profileImage))
  }

}