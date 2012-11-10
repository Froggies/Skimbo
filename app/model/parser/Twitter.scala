package model.parser

import model._
import SocialNetwork._
import org.joda.time._
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import org.joda.time.format._
import java.util.Locale
import play.data.validation.ValidationError
import org.joda.time.tz.UTCProvider
import java.text.SimpleDateFormat

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
  createdAt: DateTime)

case class TwitterTag(text: String, indices: List[Int])
case class TwitterUrl(shortUrl: String, url: String, indices: List[Int])
case class TwitterMention(authorName: String, authorScreenName: String, indices: List[Int])

object TwitterHomeTimeLine extends GenericParser[Tweet] {
  
  val tweetDetailUrl = "http://twitter.com/%s/status/%s"; 

  def asSkimbos(tweets: List[Tweet]): List[Skimbo] = {
    for (tweet <- tweets) yield Skimbo(
      tweet.authorName,
      tweet.screenName,
      tweet.text,
      tweet.createdAt,
      Nil, //TODO: Find out how to this, we obviously need to search
      // tweet where in_reply_to_status_id == tweet.id
      // but where ?
      SocialNetwork.Twitter,
      tweet.retweets,
      tweetDetailUrl.format(tweet.screenName, tweet.id),
      tweet.id
    )
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
    (__ \ "created_at").read[DateTime](twitterDateReader)
  )(Tweet.apply _)
}