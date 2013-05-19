package parser.json.providers

import org.joda.time._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.format._
import parser.json.GenericJsonParser
import models.Skimbo
import java.text.SimpleDateFormat
import services.auth.providers.Twitter
import java.util.Locale
import services.endpoints.Configuration

case class Tweet(
  id: String,
  text: String,
  source: String,
  hashTags: List[TwitterTag],
  urls: List[TwitterUrl],
  mentions: List[TwitterMention],
  medias: Option[List[TwitterMedia]],
  favorited: Boolean,
  retweets: Int,
  retweeted: Boolean,
  authorName: String,
  screenName: String,
  profileImageUrl: Option[String],
  createdAt: DateTime)

case class TwitterTag(text: String, indices: List[Int])
case class TwitterUrl(shortUrl: String, url: String, indices: List[Int])
case class TwitterMention(authorName: String, authorScreenName: String, indices: List[Int])
case class TwitterMedia(url: String, indices: List[Int], typeMedia: String)

object TwitterTimelineParser extends GenericJsonParser {

  val tweetDetailUrl = "http://twitter.com/%s/status/%s";

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[Tweet](json).fold(
      error => logParseError(json, error, "TwitterTimelineParser"),
      tweet => Some(Skimbo(
        tweet.id,
        tweet.authorName,
        tweet.screenName,
        tweet.text,
        tweet.createdAt,
        Nil,
        tweet.retweets,
        Some(tweetDetailUrl.format(tweet.screenName, tweet.id)),
        tweet.id.toString,
        tweet.profileImageUrl,
        Configuration.Twitter.wall,
        tweet.retweeted,
        tweet.medias.getOrElse(Seq.empty).map(_.url))))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:Option[String]): String = {
    if (sinceId2.isEmpty) {
      sinceId
    } else {
      if (sinceId.toLong > sinceId2.get.toLong) {
        sinceId
      } else {
        sinceId2.get
      }
    }
  }

}

object TwitterHashtagParser extends GenericJsonParser {
  val tweetDetailUrl = TwitterTimelineParser.tweetDetailUrl
  override def asSkimbo(json: JsValue): Option[Skimbo] = TwitterTimelineParser.asSkimbo(json)
  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "statuses")
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

object TwitterMedia {
  implicit val tweeterMedia: Reads[TwitterMedia] = (
    (__ \ "media_url_https").read[String] and
    (__ \ "indices").read[List[Int]] and
    (__ \ "type").read[String])(TwitterMedia.apply _)
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
    (__ \ "id_str").read[String] and
    (__ \ "text").read[String] and
    (__ \ "source").read[String] and
    (__ \ "entities" \ "hashtags").read[List[TwitterTag]] and
    (__ \ "entities" \ "urls").read[List[TwitterUrl]] and
    (__ \ "entities" \ "user_mentions").read[List[TwitterMention]] and
    (__ \ "entities" \ "media").readNullable[List[TwitterMedia]] and
    (__ \ "favorited").read[Boolean] and
    (__ \ "retweet_count").read[Int] and
    (__ \ "retweeted").read[Boolean] and
    (__ \ "user" \ "name").read[String] and
    (__ \ "user" \ "screen_name").read[String] and
    (__ \ "user" \ "profile_image_url").readNullable[String] and
    (__ \ "created_at").read[DateTime](twitterDateReader))(Tweet.apply _)
}