package googleplus

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import parser.json.providers.GoogleplusWallParser
import parser.json.providers.GoogleplusWallMessage

class GoogleplusParserTest extends Specification {

  "GoogleplusParser" should {
    "Read json from Googleplus API for notification requests" in {
      val jsonMsg = GoogleplusWallParser.cut(GoogleplusFixture.timeline)
      jsonMsg.size must beEqualTo(6)
      val msg = Json.fromJson[GoogleplusWallMessage](jsonMsg(0))
      msg.get.id must beEqualTo("z13zt1waxo2xjpgoa04cfhoxmlzvh3za0bs")
    }

    "Read json from complexe API json" in {
      val jsonMsg = GoogleplusWallParser.cut(GoogleplusFixture.timeline)
      jsonMsg.size must beEqualTo(6)
    }

    "Convert Viadeo message as Skimbo" in {
      val jsonMsg = GoogleplusWallParser.cut(GoogleplusFixture.timeline)
      val msg = Json.fromJson[GoogleplusWallMessage](jsonMsg(0)).get
      val res = GoogleplusWallParser.asSkimbo(jsonMsg(0))
      res.get.authorScreenName must beEqualTo(msg.displayName)
    }
  }

}