package parser.json.providers

import parser.json.GenericJsonParserUser
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader
import models.user.ProviderUser
import services.auth.providers.Bitbucket
import models.user.SkimboToken
import play.api.libs.oauth.RequestToken

object BitbucketUser extends GenericJsonParserUser {

  override def asProviderUser(json: JsValue)(implicit request: RequestHeader): Option[models.user.ProviderUser] = {
    asProviderUser(json, Bitbucket.getToken)
  }
  
  def asProviderUser(json: JsValue, token:Option[RequestToken]): Option[models.user.ProviderUser] = {
    val id = (json \ "user" \ "username").as[String]
    val fname = (json \ "user" \ "first_name").asOpt[String]
    val lname = (json \ "user" \ "last_name").asOpt[String]
    val displayName = Some(id)
    val description = None
    val profileImage = (json \ "user" \ "avatar").asOpt[String]
    val skimboToken = token.map(t => SkimboToken(t.token, Some(t.secret)))
    Some(ProviderUser(
        id, 
        Bitbucket.name, 
        skimboToken, 
        displayName, 
        displayName, 
        description, 
        profileImage))
  }
  
}