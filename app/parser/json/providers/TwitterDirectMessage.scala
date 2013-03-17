package parser.json.providers

import org.joda.time._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.format._
import models.Skimbo
import parser.json.GenericJsonParser
import services.auth.providers.Twitter
import services.endpoints.Configuration

case class TwitterDirectMessage(
  id: String,
  text: String,
  authorName: String,
  screenName: String,
  profileImageUrl: Option[String],
  createdAt: DateTime
)

object TwitterDirectMessageParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[TwitterDirectMessage](json).fold(
      error => logParseError(json, error, "TwitterTimelineParser"),
      tweet => Some(Skimbo(
        tweet.id,
        tweet.authorName,
        tweet.screenName,
        tweet.text,
        tweet.createdAt,
        Nil,
        -1,
        Some("http://twitter.com/"),
        tweet.id.toString,
        tweet.profileImageUrl,
        Configuration.Twitter.directMessage)))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:Option[String]): String = 
    TwitterTimelineParser.nextSinceId(sinceId, sinceId2)

}

object TwitterDirectMessage {
  val twitterDatePattern = Tweet.twitterDatePattern
  val twitterDateReader = Tweet.twitterDateReader

  implicit val tweetReader: Reads[TwitterDirectMessage] = (
    (__ \ "id_str").read[String] and
    (__ \ "text").read[String] and
    (__ \ "sender" \ "name").read[String] and
    (__ \ "sender" \ "screen_name").read[String] and
    (__ \ "sender" \ "profile_image_url").readNullable[String] and
    (__ \ "created_at").read[DateTime](twitterDateReader))(TwitterDirectMessage.apply _)
}