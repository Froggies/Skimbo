package json.parser

import org.joda.time._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.format._
import java.util.Locale
import org.joda.time.tz.UTCProvider
import java.text.SimpleDateFormat
import services.auth.providers.Twitter
import json.Skimbo

case class TwitterDirectMessage(
  id: Long,
  text: String,
  authorName: String,
  screenName: String,
  profileImageUrl: Option[String],
  createdAt: DateTime
)

object TwitterDirectMessageParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[TwitterDirectMessage](json).fold(
      error => logParseError(json, error, "TwitterTimelineParser"),
      tweet => Some(Skimbo(
        tweet.authorName,
        tweet.screenName,
        tweet.text,
        tweet.createdAt,
        Nil,
        -1,
        Some("http://twitter.com/"),
        tweet.id.toString,
        tweet.profileImageUrl,
        Twitter)))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:String): String = 
    json.parser.TwitterTimelineParser.nextSinceId(sinceId, sinceId2)

}

object TwitterDirectMessage {
  val twitterDatePattern = json.parser.Tweet.twitterDatePattern
  val twitterDateReader = json.parser.Tweet.twitterDateReader

  implicit val tweetReader: Reads[TwitterDirectMessage] = (
    (__ \ "id").read[Long] and
    (__ \ "text").read[String] and
    (__ \ "sender" \ "name").read[String] and
    (__ \ "sender" \ "screen_name").read[String] and
    (__ \ "sender" \ "profile_image_url").readOpt[String] and
    (__ \ "created_at").read[DateTime](twitterDateReader))(TwitterDirectMessage.apply _)
}