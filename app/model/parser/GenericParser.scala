package model.parser

import play.api.libs.json.Reads._
import model.Skimbo
import play.api.libs.json._

trait GenericParser {
  //for test purpose
  def cut(json:String):List[JsValue] = cut(Json.parse(json))
  
  def asSkimbo(json:JsValue): Option[Skimbo]
  def cut(json: JsValue):List[JsValue] = json.as[List[JsValue]]
}