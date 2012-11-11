package services.endpoints

import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._

object JsonRequest {

  case class UnifiedRequest(
    service: String,
    args: Option[Map[String, String]]
  )

  implicit val unifiedRequestReader: Reads[UnifiedRequest] = (
        (__ \ "service").read[String] and
        (__ \ "args").readOpt[Map[String, String]])(UnifiedRequest.apply _)
        
  implicit val writes = new Writes[UnifiedRequest] {
    def writes(ur: UnifiedRequest): JsValue = {
      val args = ur.args.getOrElse(Seq()).map { m =>
        m._1 -> JsString(m._2)
      }
      Json.obj(
        "service" -> ur.service,
        "args" -> JsObject(args.toList)
      )
    }
  }
}