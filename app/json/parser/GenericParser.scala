package json.parser

import json.Skimbo
import play.api.libs.json._
import play.api.Logger
import play.api.data.validation.ValidationError
import services.auth.GenericProvider

trait GenericParser {

  def cut(json: String): List[JsValue] = cut(Json.parse(json))

  protected def asSkimbo(json:JsValue): Option[Skimbo]

  def nextSinceId(sinceId:String, sinceId2:String): String = sinceId 

  def asSkimboSafe(json: JsValue) : Option[Skimbo] = {
    try {
      asSkimbo(json)
    } catch {
      case ex : Throwable => {
        Logger.error("Error during parsing this message", ex)
        Logger.info(json.toString)
        None
      }
    }
  }
  
  def cut(json: JsValue) = json.as[List[JsValue]]

  def cutSafe(response: play.api.libs.ws.Response, provider: GenericProvider): Option[List[JsValue]] = {
    try {
      Some(cut(provider.resultAsJson(response)))
    } catch {
      case err: play.api.libs.json.JsResultException => {
        Logger.error("Invalid message", err)
        Logger.info(response.json.toString)
        None
      }
      case err: Throwable => {
        Logger.error("Unexpected message", err)
        Logger.info(response.body)
        None
      }
    }
  }
  
  protected def logParseError(json: JsValue, errors: Seq[(JsPath, Seq[ValidationError])], where: String = "") = {
    Logger.error("Fail parsing message ("+where+")")
    Logger.info(errors.toString)
    Logger.info(json.toString)
    None
  }
}