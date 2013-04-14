package parser.json.providers

import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader
import services.auth.providers.GitHub
import models.user.SkimboToken
import parser.json.GenericJsonParserParamHelper
import models.ParamHelper

object GithubUser extends GenericJsonParserUser with GenericJsonParserParamHelper {

  val gravatarUrl = "http://www.gravatar.com/avatar/:hash?s=30"
  
  override def cut(json: JsValue) = super.cut(json \ "users")
  
  override def asProviderUser(idUser: String, json: JsValue): Option[models.user.ProviderUser] = {
    val id = (json \ "id").asOpt[Int]
    val username = (json \ "login").asOpt[String]
    val name = (json \ "name").asOpt[String]
    val description = (json \ "bio").asOpt[String]
    val profileImage = (json \ "gravatar_id").asOpt[String]
    Some(models.user.ProviderUser(
        id.getOrElse((json \ "id").as[String]).toString, 
        GitHub.name, 
        Some(SkimboToken(GitHub.getToken(idUser).get.token, None)), 
        username, 
        name, 
        description, 
        Some(gravatarUrl.replace(":hash", profileImage.getOrElse("")))))
  }
  
  override def asParamHelper(idUser: String, json: JsValue) : Option[models.ParamHelper] = {
    asProviderUser(idUser, json).map( providerUser => 
      ParamHelper(
        providerUser.name.getOrElse(providerUser.username.getOrElse("--no name--")),
        providerUser.username.getOrElse(providerUser.name.getOrElse(providerUser.id)),
        providerUser.avatar.getOrElse(""),
        providerUser.description
      )
    )
  }
  
}