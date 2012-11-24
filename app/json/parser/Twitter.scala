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

case class Tweet(
  id: Long,
  text: String,
  source: String,
  hashTags: List[TwitterTag],
  urls: List[TwitterUrl],
  mentions: List[TwitterMention],
  favorited: Boolean,
  retweets: Int,
  authorName: String,
  screenName: String,
  profileImageUrl: Option[String],
  createdAt: DateTime)

case class TwitterTag(text: String, indices: List[Int])
case class TwitterUrl(shortUrl: String, url: String, indices: List[Int])
case class TwitterMention(authorName: String, authorScreenName: String, indices: List[Int])

object TwitterTimelineParser extends GenericParser {

  val tweetDetailUrl = "http://twitter.com/%s/status/%s";

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[Tweet](json).fold(
      error => logParseError(json, error, "ViadeoWallMessage"),
      tweet => Some(Skimbo(
        tweet.authorName,
        tweet.screenName,
        tweet.text,
        tweet.createdAt,
        Nil,
        tweet.retweets,
        Some(tweetDetailUrl.format(tweet.screenName, tweet.id)),
        tweet.id.toString,
        tweet.profileImageUrl,
        Twitter)))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:String): String = {
    if(sinceId2.isEmpty()) {
      sinceId
    } else {
      if(sinceId.toLong > sinceId2.toLong) {
        sinceId
      } else {
        sinceId2
      }
    }
  }

}

object TwitterHashtagParser extends GenericParser {
  val tweetDetailUrl = TwitterTimelineParser.tweetDetailUrl
  override def asSkimbo(json: JsValue): Option[Skimbo] = TwitterTimelineParser.asSkimbo(json)
  override def cut(json: JsValue): List[JsValue] = json.\("statuses").as[List[JsValue]]
}

object TwitterTag {
  implicit val hashTagReader: Reads[TwitterTag] = (
    (__ \ "text").read[String] and
    (__ \ "indices").read[List[Int]])(TwitterTag.apply _)
}

object TwitterUrl {
  implicit val tweeterUrl: Reads[TwitterUrl] = (
    (__ \ "url").read[String] and
    (__ \ "expanded_url").read[String] and
    (__ \ "indices").read[List[Int]])(TwitterUrl.apply _)
}

object TwitterMention {
  implicit val tweeterMention: Reads[TwitterMention] = (
    (__ \ "name").read[String] and
    (__ \ "screen_name").read[String] and
    (__ \ "indices").read[List[Int]])(TwitterMention.apply _)
}

object Tweet {
  val twitterDatePattern = "EEE MMM dd HH:mm:ss Z yyyy";

  val twitterDateReader = new Reads[org.joda.time.DateTime] {
    def reads(json: JsValue): JsResult[DateTime] = {
      json match {
        case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
        case JsString(s) => {
          val sf = new SimpleDateFormat(twitterDatePattern, Locale.ENGLISH)
          sf.setLenient(true)
          JsSuccess(new DateTime(sf.parse(s)))
        }
        case _ => JsError("Error while parsing date")
      }
    }
  }

  implicit val tweetReader: Reads[Tweet] = (
    (__ \ "id").read[Long] and
    (__ \ "text").read[String] and
    (__ \ "source").read[String] and
    (__ \ "entities" \ "hashtags").read[List[TwitterTag]] and
    (__ \ "entities" \ "urls").read[List[TwitterUrl]] and
    (__ \ "entities" \ "user_mentions").read[List[TwitterMention]] and
    (__ \ "favorited").read[Boolean] and
    (__ \ "retweet_count").read[Int] and
    (__ \ "user" \ "name").read[String] and
    (__ \ "user" \ "screen_name").read[String] and
    (__ \ "user" \ "profile_image_url").readOpt[String] and
    (__ \ "created_at").read[DateTime](twitterDateReader))(Tweet.apply _)
}