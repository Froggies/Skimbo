package model.parser

import model.Skimbo
import play.api.libs.json._

trait GenericParser {
  def cut(json:String):List[JsValue] = cut(Json.parse(json))
  def asSkimbo(json:JsValue): Option[Skimbo]
  def nextSinceId(sinceId:String, sinceId2:String): String = sinceId
  def cut(json: JsValue):List[JsValue] = json.as[List[JsValue]]
}