package services.endpoints

import org.specs2.mutable.Specification
import play.api.test.Helpers._
import play.api.test.{WithApplication, FakeApplication}
import scala.concurrent.Await
import services.dao.UserDao
import scala.concurrent.duration.Duration
import models.User
import services.dao.UtilTest
import models.user._
import services.endpoints.JsonRequest.UnifiedRequest


class ConfigurationTest extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  object Util {
    def createUser(userId: String) = {
      val user = User.create(userId)
      Await.result(UserDao.add(user), Duration("10 seconds"))
      Await.result(UserDao.addAccount(userId, UtilTest.makeAccount(userId+"acc")), Duration("10 seconds"))
    }
    def addColumn(userId: String, name: String) = {
      val columnProvider1 = UtilTest.makeColumn(name, name, "test", "test")
      Await.result(UserDao.addColumn(userId, columnProvider1), Duration("10 seconds"))
    }
    def addColumnWithCustomRequest(userId: String, name: String, requests: Seq[UnifiedRequest]) = {
      val column = Column("column", requests, 0, -1, -1)
      Await.result(UserDao.addColumn(userId, column), Duration("10 seconds"))
    }
    def getConfigDelay(service: String, userId: String) = {
      val twitterWallConfig = Endpoints.getConfig(service).get
      twitterWallConfig.delay(userId)
    }
    def prepareUserTest() = {
      val userId = "usertest"
      Util.createUser(userId);
      Util.addColumn(userId, "provider1")
      userId
    }
  }

  "Twitter Endpoint configuration" should {
    "should return 0 delay between request if no service are used" in new WithApplication(FakeApplication(additionalConfiguration = testConfiguration)) {
        val userId = Util.prepareUserTest()
        Util.getConfigDelay(Configuration.Twitter.wall.uniqueName, userId) must be equalTo 0
    }
  }

  "Twitter Endpoint configuration" should {
    "compute a proper delay if services are used" in new WithApplication(FakeApplication(additionalConfiguration = testConfiguration)) {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {
        // Twitter wall is here used 7 times in 2 differents columns
        val userId = Util.prepareUserTest()
        val twitterWallName = Configuration.Twitter.wall.uniqueName
        val unifiedRequestsForCol1 = Seq(
          UnifiedRequest(twitterWallName, Some(Map())),
          UnifiedRequest(twitterWallName, Some(Map())),
          UnifiedRequest(twitterWallName, Some(Map()))
        )
        val unifiedRequestsForCol2 = Seq(
          UnifiedRequest(twitterWallName, Some(Map())),
          UnifiedRequest(twitterWallName, Some(Map())),
          UnifiedRequest(twitterWallName, Some(Map())),
          UnifiedRequest(twitterWallName, Some(Map()))
        )
        Util.addColumnWithCustomRequest(userId, "col 1", unifiedRequestsForCol1)
        Util.addColumnWithCustomRequest(userId, "col 2", unifiedRequestsForCol2)


        Util.getConfigDelay(twitterWallName, userId) must be equalTo 450
      }
    }
  }

}

