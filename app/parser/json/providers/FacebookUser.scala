package parser.json.providers

import play.api.Logger
import play.api.mvc.RequestHeader
import services.auth.providers.Facebook
import models.user.SkimboToken
import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue
import parser.json.GenericJsonParserParamHelper
import models.ParamHelper

case class FacebookUser(
    id: String,
    name: String
)

object FacebookUser extends GenericJsonParserUser with GenericJsonParserParamHelper {

  override def cut(json: JsValue) = super.cut(json \ "data")
  
  override def asProviderUser(idUser: String, json: JsValue): Option[models.user.ProviderUser] = {
      val id = (json \ "id").as[String]
      val name = (json \ "name").asOpt[String]
      val picture = (json \ "picture" \ "data" \ "url").asOpt[String]
      Some(models.user.ProviderUser(
          id, 
          Facebook.name, 
          Some(SkimboToken(Facebook.getToken(idUser).get.token, None)), 
          name, 
          name, 
          None, 
          picture))
  }
  
  override def asParamHelper(idUser: String, json: JsValue) : Option[models.ParamHelper] = {
    asProviderUser(idUser, json).map( user => ParamHelper.fromProviderUser(user))
  }

}