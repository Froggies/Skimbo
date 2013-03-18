package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json.Reads
import play.api.libs.functional.syntax._
import play.api.libs.json._
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.Scoopit
import org.joda.time.DateTimeZone
import services.endpoints.Configuration

case class ScoopitPost (
    id: Long,
    title: String,
    text: String,
    createdDate: Long,
    url: String,
    thanksCount: Int,
    iThanked: Boolean,
    displayName: String,
    avatarUrl : String
)

object ScoopitWallParser extends GenericJsonParser {

  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "posts")
  
  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[ScoopitPost](json).fold(
      error => logParseError(json, error, "ScoopitWallParser"),
      post => Some(Skimbo(
        post.id.toString,
        post.displayName,
        post.displayName,
        post.title + "<br />" + post.text,
        new DateTime(post.createdDate, DateTimeZone.UTC),
        Nil,
        post.thanksCount,
        Some(post.url),
        post.createdDate.toString,
        Some(post.avatarUrl),
        Configuration.Scoopit.wall,
        post.iThanked)))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:Option[String]): String = 
    DateTime.now(DateTimeZone.UTC).toInstant().getMillis().toString
}

object ScoopitPost {
  implicit val ScoopitPostReader: Reads[ScoopitPost] = (
    (__ \ "id").read[Long] and
    (__ \ "title").read[String] and
    (__ \ "htmlContent").read[String] and
    (__ \ "publicationDate").read[Long] and
    (__ \ "url").read[String] and
    (__ \ "thanksCount").read[Int] and
    (__ \ "thanked").read[Boolean] and
    (__ \ "author" \ "name").read[String] and
    (__ \ "author" \ "avatarUrl").read[String])(ScoopitPost.apply _)
}