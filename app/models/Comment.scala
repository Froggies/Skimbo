package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Comment(
    message: String,
    providerId: String,
    serviceName: String,
    columnTitle: String
)

object Comment {
  implicit val reader = (
    (__ \ "message").read[String] and
    (__ \ "providerId").read[String] and
    (__ \ "serviceName").read[String] and
    (__ \ "columnTitle").read[String])(Comment.apply _)
}