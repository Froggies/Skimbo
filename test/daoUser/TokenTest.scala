package daoUser

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import controllers.UserDao
import models.User
import models.user.Account
import java.util.Date
import scala.concurrent.Await
import java.util.concurrent.TimeoutException
import scala.concurrent.ExecutionContext.Implicits.global
import models.user.Column
import services.endpoints.JsonRequest._
import models.user.ProviderUser
import scala.concurrent.duration.Duration
import services.auth.providers.Twitter
import models.user.SkimboToken


object TokenTest extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  "DaoUser" should {
    "Add, find and delete a user" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {

        val id = "test"
        val user = UtilTest.makeUser(id)

        test1(id, user)
        test2(id, user)

        Await.result(UserDao.delete(user), Duration("10 seconds"))
        Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
      }
    }
  }
  
  /**
   * Test 1 : add and addToken
   */
  def test1(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //Find user
    val optionUser: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser must not be beNone
    optionUser.get.accounts.size must beEqualTo(1)
    optionUser.get.accounts(0).id must beEqualTo(id)
    //Add token
    Await.result(UserDao.setToken(id, Twitter, SkimboToken("test", None)), Duration("10 seconds"))
  }
  
  /**
   * Test 2 : find, modToken and check
   */
  def test2(id: String, user: User) {
    //Find user
    val optionUser: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser must not be beNone
    optionUser.get.accounts.size must beEqualTo(1)
    optionUser.get.accounts(0).id must beEqualTo(id)
    //Add token
    Await.result(UserDao.setToken(id, Twitter, SkimboToken("test2", None)), Duration("10 seconds"))
    //Has token
    val token = Await.result(UserDao.getToken(id, Twitter), Duration("10 seconds"))
    token must not be beNone
    token.get.token must beEqualTo("test2")
  }
  
}