package facebook

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo
import play.api.libs.json.Json

object FacebookParserTest extends Specification {

  "FacebookParser" should {
    "Read json from facebook API for wall requests" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.miniTimeline)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[FacebookWallMessage](jsonMsg(0)).get
      msg.fromName must beEqualTo("Olivier Clavel")
      msg.link.get must beEqualTo("https://www.facebook.com/644378010/posts/10151239069468011")
    }

    "Read json from complexe API json" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.timeline)
      jsonMsg.size must beEqualTo(25)
    }

    "Convert Facebook message as Skimbo" in {
      val jsonMsg = FacebookWallParser.cut(FacebookFixture.timeline)
      val msg = Json.fromJson[FacebookWallMessage](jsonMsg(0)).get
      val res = FacebookWallParser.asSkimbo(msg)
      res must beSome
      res.get.directLink must beEqualTo(msg.link)
      res.get.message must beEqualTo(msg.message.get)
    }
  }

}