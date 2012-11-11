package test.twitter

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo

object TwitterParserTest extends Specification {

	"TwitterParser" should {
		"Retrieve correct values" in {
			val tweets = TwitterTimelineParser.from(TwitterFixture.fullMessage)
			tweets.head.authorName must equalTo("Yvonnick Esnault")
			tweets.head.urls.head.shortUrl must equalTo("http://t.co/HZCphh5J")
			tweets.head.mentions.last.authorScreenName must equalTo("ybonnel")
			tweets.head.createdAt.toString("MM-dd-yyyy") must equalTo("10-24-2012")
			
			val skimbos = TwitterTimelineParser.asSkimbos(tweets)
			val expectedId = "261219035964915700"
			
			skimbos.head.sinceId must equalTo(expectedId)
			skimbos.head.directLink.get must equalTo("http://twitter.com/yesnault/status/261219035964915700")
		}
	}
	
}						