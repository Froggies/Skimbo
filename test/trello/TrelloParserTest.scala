package trello

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo

object TrelloParserTest extends Specification {

  "TrelloParser" should {
    "Read json from trello API for notification requests" in {
      val messages = TrelloWallParser.from(TrelloFixture.miniTimeline)
      messages.size must beEqualTo(1)
      messages(0).idMemberCreator must beEqualTo("501cf5007d3f7888395dbf71")
      messages(0).unread must beEqualTo(false)
    }

    "Read json from complexe API json" in {
      val messages = TrelloWallParser.from(TrelloFixture.timeline)
      messages.size must beEqualTo(32)
    }

    "Convert Trello message as Skimbo" in {
      val messages = TrelloWallParser.from(TrelloFixture.miniTimeline)
      val res = TrelloWallParser.asSkimbos(messages)
      messages.size must beEqualTo(res.size)
      res(0).authorScreenName must beEqualTo(messages(0).memberCreator.username)
    }
  }

}