package parser.json.providers

import models.Skimbo
import play.api.libs.json.JsValue
import parser.json.GenericJsonParser
import play.api.libs.json._

object ScoopitTopic extends GenericJsonParser {

  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "topic" \ "curatedPosts")
  
  override def asSkimbo(json: JsValue): Option[Skimbo] = ScoopitWallParser.asSkimbo(json)
  
  override def nextSinceId(sinceId:String, sinceId2:Option[String]): String = ScoopitWallParser.nextSinceId(sinceId, sinceId2)

}