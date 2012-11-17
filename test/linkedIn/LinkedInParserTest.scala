package linkedIn

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo
import play.api.libs.json.Json

object LinkedInParserTest extends Specification {

  "LinkedInParser" should {
    "Read json from linkedIn API for notification requests" in {
      val jsonMsg = LinkedInWallParser.cut(LinkedInFixture.miniTimeline)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[LinkedInWallMessage](jsonMsg(0)).get
      msg.numLikes must beNone
      msg.updateType must beEqualTo("CONN")
    }

    "Read json from complexe API json" in {
      val jsonMsg = LinkedInWallParser.cut(LinkedInFixture.timeline)
      jsonMsg.size must beEqualTo(50)
    }

    "Convert LinkedIn message as Skimbo" in {
      val jsonMsg = LinkedInWallParser.cut(LinkedInFixture.timeline)
      val msg = Json.fromJson[LinkedInWallMessage](jsonMsg(0)).get
      val res = LinkedInWallParser.asSkimbo(msg)
      res must beSome
      res.get.authorScreenName must beEqualTo(msg.person.get.lastName)
    }
  }

}