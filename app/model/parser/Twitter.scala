package model.parser

import model._
import SocialNetwork._
import org.joda.time._
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._

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
  createdAt: String)

case class TwitterTag(text: String, indices: List[Int])
case class TwitterUrl(shortUrl: String, url: String, indices: List[Int])
case class TwitterMention(authorName: String, authorScreenName: String, indices: List[Int])

object Tweets {

  def from(json: JsValue): List[Tweet] = {
    json.as[List[Tweet]]
  }

  def from(json: String): List[Tweet] = {
    from(Json.parse(json))
  }

  def asSkimbos(tweets: List[Tweet]): List[Skimbo] = {
    for (tweet <- tweets) yield Skimbo(
      tweet.authorName,
      tweet.screenName,
      tweet.text,
      new DateTime(), //FIXME: when json is parsed correctly
      Nil, //TODO: Find out how to this, we obviously need to search
      // tweet where in_reply_to_status_id == tweet.id
      // but where ?
      SocialNetwork.Twitter,
      tweet.retweets)
  }
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

  //TODO: find out how to use custom joda time parser
  //to specify twitter pattern
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
    (__ \ "created_at").read[String])(Tweet.apply _)
}