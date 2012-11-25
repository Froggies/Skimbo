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

object UtilTest {
  
  def makeUser(id:String) = {
    User.create(id)
  }
  
  def makeAccount(id:String) = {
    Account(id, new Date())
  }
  
  def makeColumn(title:String, urService:String, urArg1:String, urArg2:String) = {
    Column(title, Seq(UnifiedRequest(urService, Some(Map(urArg1 -> urArg2)))))
  }
  
}


object TokenTest extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  "DaoUser" should {
    "Add, find and delete a user" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {

        val id = "test"
        val user = UtilTest.makeUser(id)

        test1(id, user)

        Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
      }
    }
  }
  
  /**
   * Test 1 : add, find and delete
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
    //Delete user
    //Await.result(UserDao.delete(optionUser.get), Duration("10 seconds"))
    //Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }
  
}