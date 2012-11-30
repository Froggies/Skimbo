package services.endpoints

import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonRequest {

  case class UnifiedRequest(
    service: String,
    args: Option[Map[String, String]]
  )

  implicit val unifiedRequestReader: Reads[UnifiedRequest] = (
    (__ \ "service").read[String] and
    (__ \ "args").readOpt[Map[String, String]])(UnifiedRequest)
        
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