package parser.json.detail

import parser.json.providers.ScoopitWallParser
import models.Skimbo
import play.api.libs.json.JsValue
import parser.json.GenericJsonParser
import parser.json.providers.LinkedInWallParser

object LinkedInPostDetails extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = LinkedInWallParser.asSkimbo(json)
  
  override def cut(json: JsValue) = LinkedInWallParser.cut(json)
  
}