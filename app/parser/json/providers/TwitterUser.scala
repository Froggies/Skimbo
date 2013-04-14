package parser.json.providers

import play.api.Logger
import play.api.mvc.RequestHeader
import services.auth.providers.Twitter
import models.user.SkimboToken
import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue
import parser.json.GenericJsonParserParamHelper
import models.ParamHelper

object TwitterUser extends GenericJsonParserUser with GenericJsonParserParamHelper {

  override def cut(json: JsValue) = json.as[List[JsValue]]
  
  override def asProviderUser(idUser: String, json: JsValue): Option[models.user.ProviderUser] = {
      val id = (json \ "id").as[Int].toString
      val username = (json \ "screen_name").asOpt[String]
      val name = (json \ "name").asOpt[String]
      val description = (json \ "description").asOpt[String]
      val profileImage = (json \ "profile_image_url").asOpt[String]
      val token = Twitter.getToken(idUser)
      Some(models.user.ProviderUser(
          id, 
          Twitter.name, 
          Some(SkimboToken(token.get.token, Some(token.get.secret))), 
          username, 
          name, 
          description, 
          profileImage))
  }
  
  override def asParamHelper(idUser: String, json: JsValue) : Option[models.ParamHelper] = {
    asProviderUser(idUser, json).map( user => ParamHelper.fromProviderUser(user))
  }

}