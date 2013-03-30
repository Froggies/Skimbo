package parser.json.detail

import parser.json.GenericJsonParser
import play.api.libs.json.JsValue
import models.Skimbo
import parser.json.providers.GoogleplusWallParser

object GoogleplusDetails extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = GoogleplusWallParser.asSkimbo(json)
  
  override def cut(json: JsValue) = List(json)
  
}