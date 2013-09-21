package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

case class UnifiedRequest(
    service: String,
    args: Option[Map[String, String]]
)

object UnifiedRequest {

  implicit val unifiedRequestReader: Reads[UnifiedRequest] = (
    (__ \ "service").read[String] and
    (__ \ "args").readNullable[Map[String, String]])(UnifiedRequest.apply _)
        
  implicit val writes = new Writes[UnifiedRequest] {
    def writes(unifiedRequest: UnifiedRequest): JsValue = {
      val args = unifiedRequest.args.map(_.mapValues(JsString(_)).toSeq)
      Json.obj(
        "service" -> unifiedRequest.service,
        "args" -> JsObject(args.getOrElse(Seq.empty))
      )
    }
  }
}