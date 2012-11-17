package model.parser

import play.api.libs.json.Reads._
import model.Skimbo
import play.api.libs.json._

trait GenericParser[T] {
  //for test purpose
  def cut(json:String):List[JsValue] = cut(Json.parse(json))
  
  def asSkimbo(element: T): Option[Skimbo]
  def cut(json: JsValue):List[JsValue]
  def transform(json:JsValue):JsValue
}