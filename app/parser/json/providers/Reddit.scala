package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.GenericParser
import parser.json.GenericJsonParser
import models.Skimbo
import services.endpoints.Configuration
import org.joda.time.DateTimeUtils
import org.joda.time.DateTimeZone

case class RedditMessage (
  id: String,
  author: String,
  msgType: String,
  score: Int,
  nbComments: Int,
  createdAt: Long,
  url: Option[String],
  permalink: String,
  title: String,
  message: String,
  media: Option[String]
)

object RedditMessageParser extends GenericJsonParser {
  
  val redditUrl = "http://www.reddit.com/"
  
  override def asSkimbo(json:JsValue): Option[Skimbo] = {
    Json.fromJson[RedditMessage](json).fold(
      error => logParseError(json, error, "RedditMessageParser"),
      e => if(e.msgType == "t3" || e.msgType == "t4") {//retain link and message
        Some(Skimbo(
          e.msgType + "_" + e.id,
          e.author,
          e.author,
          e.title + "\n\n" + e.message,
          new DateTime(e.createdAt, DateTimeZone.UTC),
          Nil,
          e.score,
          e.url.orElse(Some(redditUrl + e.permalink)),
          e.msgType + "_" + e.id,
          None,
          Configuration.Reddit.hot,
          false
        ))
      } else {
        None
      }
    )
  }

  override def cut(json: JsValue) = {
    val t = super.cut(json \ "data" \ "children")
    logParseError(JsString(""), Seq.empty, "REDDIT size : " + t.size);
    t
  }
}

object RedditMessage {
  implicit val redditMessageReader: Reads[RedditMessage] = (
    (__ \ "data" \ "id").read[String] and
    (__ \ "data" \ "author").read[String] and
    (__ \ "kind").read[String] and
    (__ \ "data" \ "score").read[Int] and
    (__ \ "data" \ "num_comments").read[Int] and
    (__ \ "data" \ "created_utc").read[Long] and
    (__ \ "data" \ "url").readNullable[String] and
    (__ \ "data" \ "permalink").read[String] and
    (__ \ "data" \ "title").read[String] and
    (__ \ "data" \ "selftext").read[String] and
    (__ \ "data" \ "media").readNullable[String])(RedditMessage.apply _)
}