package test.twitter

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._

object TwitterParserTest extends Specification {

	"TwitterParser" should {
		"Retrieve correct values" in {
			val tweets = Tweets.from(TwitterFixture.fullMessage)
			tweets.head.authorName must equalTo("Yvonnick Esnault")
			tweets.head.urls.head.shortUrl must equalTo("http://t.co/HZCphh5J")
			tweets.head.mentions.last.authorScreenName must equalTo("ybonnel")
		}
	}
	
}						