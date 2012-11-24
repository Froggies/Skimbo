package json.parser

import json.Skimbo
import play.api.libs.json._
import play.api.Logger

trait GenericParser {
  def cut(json: String): List[JsValue] = cut(Json.parse(json))
  def cut(json: JsValue): List[JsValue] = json.as[List[JsValue]]
  protected def asSkimbo(json:JsValue): Option[Skimbo]
  def nextSinceId(sinceId:String, sinceId2:String): String = sinceId 
  
  def asSkimboSafe(json: JsValue) : Option[Skimbo] = {
    try {
      asSkimbo(json)
    } catch {
      case _ : Throwable => {
        Logger.error("Error during parsing this message")
        Logger.error(json.toString)
        None
      }
    }
  }
}