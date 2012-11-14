package linkedIn

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import model.parser._
import model.Skimbo

object LinkedInParserTest extends Specification {

  "LinkedInParser" should {
    "Read json from linkedIn API for notification requests" in {
      val messages = LinkedInWallParser.from(LinkedInFixture.miniTimeline)
      messages.size must beEqualTo(1)
      messages(0).numLikes must beNone
      messages(0).updateType must beEqualTo("CONN")
    }

    "Read json from complexe API json" in {
      val messages = LinkedInWallParser.from(LinkedInFixture.timeline)
      messages.size must beEqualTo(10)
      messages(4).person must beNone
      messages(4).companyName.get must beEqualTo("Take a Tea")
      messages(4).companyPerson must beSome
      messages(4).companyPerson.get.person.lastName must beEqualTo("Roux")
    }

    "Convert LinkedIn message as Skimbo" in {
      val messages = LinkedInWallParser.from(LinkedInFixture.timeline)
      val res = LinkedInWallParser.asSkimbos(messages)
      messages.size must beEqualTo(res.size)
      res(0).authorScreenName must beEqualTo(messages(0).person.get.lastName)
      res(5).message must beEqualTo("Camille")
    }
  }

}