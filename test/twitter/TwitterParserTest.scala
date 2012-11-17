package test.twitter

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo
import play.api.libs.json.Json
import model.parser.Tweet

object TwitterParserTest extends Specification {

	"TwitterParser" should {
		"Retrieve correct values" in {
			val jsonTweet = TwitterTimelineParser.cut(TwitterFixture.fullMessage)
			val tweet = Json.fromJson[Tweet](jsonTweet.head).get
			tweet.authorName must equalTo("Yvonnick Esnault")
			tweet.urls.head.shortUrl must equalTo("http://t.co/HZCphh5J")
			tweet.mentions.last.authorScreenName must equalTo("ybonnel")
			tweet.createdAt.toString("MM-dd-yyyy") must equalTo("10-24-2012")
			
			val skimbo = TwitterTimelineParser.asSkimbo(tweet).get
			val expectedId = "261219035964915700"
			
			skimbo.sinceId must equalTo(expectedId)
			skimbo.directLink.get must equalTo("http://twitter.com/yesnault/status/261219035964915700")
		}
	}
	
}						