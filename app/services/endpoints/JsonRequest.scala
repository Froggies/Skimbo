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
}