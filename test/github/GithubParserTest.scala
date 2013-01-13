package github

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import json.parser._
import json.Skimbo
import play.api.libs.json.Json

class GithubParserTest extends Specification {

  "GithubParser" should {
    "Read json from github API for notification requests" in {
      val jsonMsg = GithubWallParser.cut(GithubFixture.miniTimeLineForkEvent)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[GithubWallMessage](jsonMsg(0))
      msg.get.id must beEqualTo("1626921353")
    }

    "Read json from github API for notification requests" in {
      val jsonMsg = GithubWallParser.cut(GithubFixture.miniTimeLinePushEvent)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[GithubWallMessage](jsonMsg(0))
      msg.get.id must beEqualTo("1607692892")
    }

    "Read json from complexe API json" in {
      val jsonMsg = GithubWallParser.cut(GithubFixture.timeline)
      jsonMsg.size must beEqualTo(12)
      val githubMsg = jsonMsg.map { jmsg =>
        GithubWallParser.asSkimbo(jmsg).get
      }
    }

    "Convert Github message as Skimbo" in {
      val jsonMsg = GithubWallParser.cut(GithubFixture.miniTimeLineForkEvent)
      val msg = Json.fromJson[GithubWallMessage](jsonMsg(0))
      val res = GithubWallParser.asSkimbo(jsonMsg(0))
      res.get.authorScreenName must beEqualTo(msg.get.actorLogin)
    }
  }

}