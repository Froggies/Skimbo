package facebook

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import parser.json.providers.FacebookWallParser
import parser.json.providers.FacebookWallMessage

object FacebookParserTest extends Specification {

  "FacebookParser" should {
    "Read json from facebook API for wall requests" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.miniTimeline)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[FacebookWallMessage](jsonMsg(0)).get
      msg.fromName must beEqualTo("Olivier Clavel")
      msg.link must beEqualTo("https://www.facebook.com/644378010/posts/10151239069468011")
    }

    "Read json from complexe API json" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.timeline)
      jsonMsg.size must beEqualTo(25)
    }

    "Convert Facebook message as Skimbo" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.timeline)
      val msg = Json.fromJson[FacebookWallMessage](jsonMsg(0)).get
      val res = FacebookWallParser.asSkimbo(jsonMsg(0))
      res must beSome

      res.get.directLink.get must beEqualTo(msg.link)
      res.get.message must beEqualTo(msg.message.get)
    }
  }

}