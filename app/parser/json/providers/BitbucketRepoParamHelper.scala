package parser.json.providers

import parser.json.GenericJsonParserParamHelper
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader
import models.ParamHelper

object BitbucketRepoParamHelper extends GenericJsonParserParamHelper {

  override def cut(json: JsValue) = json.as[List[JsValue]]
  
  override def asParamHelper(json: JsValue)(implicit request: RequestHeader) : Option[models.ParamHelper] = {
    Some(ParamHelper(
      (json \ "label").as[String],
      (json \ "value").as[String],
      (json \ "avatar").as[String],
      None
    ))
  }
  
}