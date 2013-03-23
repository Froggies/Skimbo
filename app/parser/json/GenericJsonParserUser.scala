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

  val log = Logger("GenericJsonParserUser")
  
  def getProviderUser(response:play.api.libs.ws.Response, provider: GenericProvider)(implicit request: RequestHeader):Option[List[models.user.ProviderUser]] = {
    cutSafe(response, provider).map { exploded =>
      exploded.map(json => asProviderUserSafe(json)).flatten
    }
  }
  
  def cut(json: String): List[JsValue] = cut(Json.parse(json))

  def cut(json: JsValue) = json.as[List[JsValue]]

  def cutSafe(response: play.api.libs.ws.Response, provider: GenericProvider): Option[List[JsValue]] = {
    try {
      Some(cut(provider.resultAsJson(response)))
    } catch {
      case err: play.api.libs.json.JsResultException => {
        log.error("Invalid message", err)
        log.info(response.status + response.json.toString)
        None
      }
      case err: Throwable => {
        log.error("Unexpected message", err)
        log.info(response.body)
        None
      }
    }
  }
  
  def asProviderUserSafe(json: JsValue)(implicit request: RequestHeader) : Option[models.user.ProviderUser] = {
    try {
      asProviderUser(json)
    } catch {
      case ex : Throwable => {
        log.error("Error during parsing this message", ex)
        log.info(json.toString)
        None
      }
    }
  }
  
  def asProviderUser(json: JsValue)(implicit request: RequestHeader) : Option[models.user.ProviderUser]
  
}