package viadeo

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import json.parser._
import json.Skimbo
import play.api.libs.json.Json

class ViadeoParserTest extends Specification {

  "ViadeoParser" should {
    "Read json from viadeo API for notification requests" in {
      val jsonMsg = ViadeoWallParser.cut(ViadeoFixture.miniTimeline)
      jsonMsg.size must beEqualTo(1)
      val msg = Json.fromJson[ViadeoWallMessage](jsonMsg(0))
      msg.get.id must beEqualTo("vtpdaEiowDurIvlcEmydfDjnkO")
      msg.get.likeCount must beEqualTo(0)
    }

    "Read json from complexe API json" in {
      val jsonMsg = ViadeoWallParser.cut(ViadeoFixture.timeline)
      jsonMsg.size must beEqualTo(17)
      val viadeoMsg = jsonMsg.map{ jmsg => 
        val msg = Json.fromJson[ViadeoWallMessage](jmsg)
        if(msg.asOpt.isDefined) {
          ViadeoWallParser.asSkimbo(msg.get)
        } else {
          //println("MESSAGE == "+jmsg)
        }
      }
    }

    "Convert Viadeo message as Skimbo" in {
      val jsonMsg = ViadeoWallParser.cut(ViadeoFixture.miniTimeline)
      val msg = Json.fromJson[ViadeoWallMessage](jsonMsg(0)).get
      val res = ViadeoWallParser.asSkimbo(msg)
      res.get.authorScreenName must beEqualTo(msg.fromName)
    }
  }

}