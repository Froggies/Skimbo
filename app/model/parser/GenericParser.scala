package model.parser

import play.api.libs.json.Reads._
import model.Skimbo
import play.api.libs.json._

trait GenericParser[T] {
  def from(json: JsValue)(implicit fjs: Reads[T]): List[T] = json.as[List[T]]
  def from(json: String)(implicit fjs: Reads[T]): List[T] = from(Json.parse(json))
  def asSkimbos(elements: List[T]): List[Skimbo]
  def asSkimbo(element: T): Option[Skimbo]
  
  def cut(json: JsValue):List[JsValue]
  def transform(json:JsValue):JsValue
}