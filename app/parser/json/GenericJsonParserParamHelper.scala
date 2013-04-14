package parser.json

import parser.GenericParserParamHelper
import play.api.Logger
import services.auth.GenericProvider
import play.api.mvc.RequestHeader
import play.api.libs.json.JsValue
import play.api.libs.json.Json

trait GenericJsonParserParamHelper extends GenericParserParamHelper {

  override def getParamsHelper(idUser: String, response:play.api.libs.ws.Response, provider: GenericProvider):Option[List[models.ParamHelper]] = {
    cutSafe(response, provider).map { exploded =>
      exploded.map(json => asParamHelperSafe(idUser, json)).flatten
    }
  }
  
  def cut(json: JsValue) = json.as[List[JsValue]]

  private def cutSafe(response: play.api.libs.ws.Response, provider: GenericProvider): Option[List[JsValue]] = {
    try {
      Some(cut(provider.resultAsJson(response)))
    } catch {
      case err: play.api.libs.json.JsResultException => {
        Logger("GenericJsonParserParamHelper").error("Invalid message", err)
        Logger("GenericJsonParserParamHelper").info(response.status + response.json.toString)
        None
      }
      case err: Throwable => {
        Logger("GenericJsonParserParamHelper").error("Unexpected message", err)
        Logger("GenericJsonParserParamHelper").info(response.body)
        None
      }
    }
  }
  
  def asParamHelperSafe(idUser: String, json: JsValue) : Option[models.ParamHelper] = {
    try {
      asParamHelper(idUser, json)
    } catch {
      case ex : Throwable => {
        Logger("GenericJsonParserParamHelper").error("Error during parsing this message", ex)
        Logger("GenericJsonParserParamHelper").info(json.toString)
        None
      }
    }
  }
  
  def asParamHelper(idUser: String, json: JsValue): Option[models.ParamHelper]
  
}