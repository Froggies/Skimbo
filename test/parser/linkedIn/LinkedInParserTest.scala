package linkedIn

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import parser.json.providers.LinkedInWallParser
import parser.json.providers.LinkedInWallMessage

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
      msg.person.get.connections.head.firstName must beEqualTo("Andrea")
      val res = LinkedInWallParser.asSkimbo(jsonMsg(0))
      res must beSome

      res.get.authorScreenName must beEqualTo(LinkedInWallParser.getName(msg))
    }
  }

}