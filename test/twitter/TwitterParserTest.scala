package test.twitter

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import json.parser._
import json.Skimbo
import play.api.libs.json.Json

object TwitterParserTest extends Specification {

	"TwitterParser" should {
		"parse wall messages" in {
			val jsonTweet = TwitterTimelineParser.cut(TwitterFixture.fullMessage)
			val tweet = Json.fromJson[Tweet](jsonTweet.head).get
			tweet.authorName must equalTo("Yvonnick Esnault")
			tweet.urls.head.shortUrl must equalTo("http://t.co/HZCphh5J")
			tweet.mentions.last.authorScreenName must equalTo("ybonnel")
			tweet.createdAt.toString("MM-dd-yyyy") must equalTo("10-24-2012")
			
			val skimbo = TwitterTimelineParser.asSkimbo(jsonTweet(0)).get
			val expectedId = "261219035964915700"
			
			skimbo.sinceId must equalTo(expectedId)
			skimbo.directLink.get must equalTo("http://twitter.com/yesnault/status/261219035964915700")
		}
		
		"parse hashtag messages" in {
		  // Parse one message
		  val jsonTweet = TwitterHashtagParser.cut(TwitterHashTagFixture.short)
		  val tweet = Json.fromJson[Tweet](jsonTweet.head).get
		  tweet.authorName must equalTo("Nathan Outlaw")
		  
		  // Transform all messages
		  val allTweets = TwitterHashtagParser.cut(TwitterHashTagFixture.full)
		  val skimbos = allTweets.map(tweet => TwitterTimelineParser.asSkimbo(tweet))
		  skimbos.size must equalTo(7)
		}
	}
	
}						