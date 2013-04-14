package parser.json.providers

import play.api.Logger
import play.api.mvc.RequestHeader
import services.auth.providers.GooglePlus
import models.user.SkimboToken
import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue
import parser.json.GenericJsonParserParamHelper
import models.ParamHelper

object GoogleplusUser extends GenericJsonParserUser with GenericJsonParserParamHelper {

  override def cut(json: JsValue) = super.cut(json \ "items")
  
  override def asProviderUser(idUser: String, json: JsValue): Option[models.user.ProviderUser] = {
    val id = (json \ "id").as[String]
    val username = (json \ "displayName").asOpt[String]
    val name = (json \ "name" \ "familyName").asOpt[String]
    val description = Some("")
    val profileImage = (json \ "image" \ "url").asOpt[String]
    Some(models.user.ProviderUser(
        id, 
        GooglePlus.name, 
        Some(SkimboToken(GooglePlus.getToken(idUser).get.token, None)), 
        username, 
        name, 
        description, 
        profileImage))
  }
  
  override def asParamHelper(idUser: String, json: JsValue) : Option[models.ParamHelper] = {
    asProviderUser(idUser, json).map( user => ParamHelper.fromProviderUser(user))
  }
  
}