package facebook

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo

object FacebookParserTest extends Specification {

  "FacebookParser" should {
    "Read json from facebook API for wall requests" in {
      val messages = FacebookWallParser.from(FacebookFixture.miniTimeline)
      messages.size must beEqualTo(1)
      messages(0).fromName must beEqualTo("Olivier Clavel")
      messages(0).link.get must beEqualTo("https://www.facebook.com/644378010/posts/10151239069468011")
    }

    "Read json from complexe API json" in {
      val messages = FacebookWallParser.from(FacebookFixture.timeline)
      messages.size must beEqualTo(25)
    }

    "Convert Facebook message as Skimbo" in {
      val messages = FacebookWallParser.from(FacebookFixture.miniTimeline)
      val res = FacebookWallParser.asSkimbos(messages)
      messages.size must beEqualTo(res.size)
      res(0).directLink must beEqualTo(messages(0).link)
      res(0).message must beEqualTo(messages(0).message.get)
    }
  }

}