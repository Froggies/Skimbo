package parser.json.providers

import play.api.libs.json._
import models.Skimbo
import parser.json.GenericJsonParser
import services.auth.providers.Twitter
import services.endpoints.Configuration

object TwitterConnect {

}

object TwitterConnectParser extends GenericJsonParser {

  val tweetDetailUrl = "http://twitter.com/%s/status/%s";

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[Tweet](json).fold(
      error => logParseError(json, error, "TwitterTimelineParser"),
      tweet => Some(Skimbo(
        "",
        tweet.authorName,
        tweet.screenName,
        tweet.text,
        tweet.createdAt,
        Nil,
        tweet.retweets,
        Some(tweetDetailUrl.format(tweet.screenName, tweet.id)),
        tweet.id.toString,
        tweet.profileImageUrl,
        Configuration.Twitter.wall)))
  }
  
  override def nextSinceId(sinceId:String, sinceId2:Option[String]): String = TwitterTimelineParser.nextSinceId(sinceId, sinceId2)

}