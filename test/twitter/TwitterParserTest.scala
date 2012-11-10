package test.twitter

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo

object TwitterParserTest extends Specification {

	"TwitterParser" should {
		"Retrieve correct values" in {
			val tweets = Tweets.from(TwitterFixture.fullMessage)
			tweets.head.authorName must equalTo("Yvonnick Esnault")
			tweets.head.urls.head.shortUrl must equalTo("http://t.co/HZCphh5J")
			tweets.head.mentions.last.authorScreenName must equalTo("ybonnel")
			
			val skimbos = Tweets.asSkimbos(tweets)
			val expectedId: Long = 261219035964915700L
			
			skimbos.head.sinceId must equalTo(expectedId)
			skimbos.head.directLink must equalTo("http://twitter.com/yesnault/status/261219035964915700")
		}
	}
	
}						