package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Post (
  title:String,
  message:String,
  url:Option[String] = None,
  url_image:Option[String] = None
)

object Post {
  implicit val reader = (
    (__ \ "title").read[String] and
    (__ \ "message").read[String] and
    (__ \ "url").readNullable[String] and
    (__ \ "url_image").readNullable[String])(Post.apply _)
}