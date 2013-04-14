package parser.json

import play.api.libs.json.JsValue
import services.auth.GenericProvider
import play.api.libs.ws.Response
import models.user.ProviderUser
import play.api.Logger
import parser.GenericParserUser
import play.api.libs.json.Json
import play.api.mvc.RequestHeader

trait GenericJsonParserUser extends GenericParserUser {

  override def getProviderUser(idUser: String, response:play.api.libs.ws.Response, provider: GenericProvider):Option[List[models.user.ProviderUser]] = {
    cutSafe(response, provider).map { exploded =>
      exploded.map(json => asProviderUserSafe(idUser, json)).flatten
    }
  }
  
  def cut(json: JsValue) = json.as[List[JsValue]]

  private def cutSafe(response: play.api.libs.ws.Response, provider: GenericProvider): Option[List[JsValue]] = {
    try {
      Some(cut(provider.resultAsJson(response)))
    } catch {
      case err: play.api.libs.json.JsResultException => {
        Logger("GenericJsonParserUser").error("Invalid message", err)
        Logger("GenericJsonParserUser").info(response.status + response.json.toString)
        None
      }
      case err: Throwable => {
        Logger("GenericJsonParserUser").error("Unexpected message", err)
        Logger("GenericJsonParserUser").info(response.body)
        None
      }
    }
  }
  
  def asProviderUserSafe(idUser: String, json: JsValue) : Option[models.user.ProviderUser] = {
    try {
      asProviderUser(idUser, json)
    } catch {
      case ex : Throwable => {
        Logger("GenericJsonParserUser").error("Error during parsing this message", ex)
        Logger("GenericJsonParserUser").info(json.toString)
        None
      }
    }
  }
  
  def asProviderUser(idUser: String, json: JsValue) : Option[models.user.ProviderUser]
  
}