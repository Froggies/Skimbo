package parser.json.providers

import play.api.libs.json.JsValue
import models.ParamHelper
import parser.json.GenericJsonParserParamHelper

object ScoopitTopicParamHelper extends GenericJsonParserParamHelper {

  override def cut(json: JsValue) = (json \ "topics").as[List[JsValue]]

  override def asParamHelper(idUser: String, json: JsValue): Option[models.ParamHelper] = {
    Some(ParamHelper(
      (json \ "name").as[String],
      (json \ "id").as[Int] + "",
      (json \ "mediumImageUrl").as[String],
      (json \ "description").asOpt[String]))
  }

}