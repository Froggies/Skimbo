package json.parser

import json.Skimbo
import play.api.libs.json._
import play.api.Logger
import play.api.data.validation.ValidationError

trait GenericParser {
  def cut(json: String): List[JsValue] = cut(Json.parse(json))
  def cut(json: JsValue): List[JsValue] = json.as[List[JsValue]]
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
  
  protected def logParseError(json: JsValue, errors: Seq[(JsPath, Seq[ValidationError])], where: String = "") = {
    Logger.error("Fail parsing message ("+where+")")
    Logger.info(errors.toString)
    Logger.info(json.toString)
    None
  }
}