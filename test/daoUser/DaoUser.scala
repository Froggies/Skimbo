package daoUser

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import services.UserDao
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

object DaoUserSimpleFind extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  "DaoUser" should {
    "Add, find and delete a user" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {

        val id = "test"
        val user = UtilTest.makeUser(id)

        test1(id, user)
        test2(id, user)
        test3(id, user)
        test4(id, user)
        test5(id, user)
        test6(id, user)

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
    //Delete user
    Await.result(UserDao.delete(optionUser.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 2 : add, mod, find and delete
   */
  def test2(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val id2 = "test2.1"
    Await.result(UserDao.addAccount(user, UtilTest.makeAccount(id)), Duration("10 seconds"))
    Await.result(UserDao.addAccount(user, UtilTest.makeAccount(id2)), Duration("10 seconds"))
    //Find user
    val optionUser2: Option[User] = Await.result(UserDao.findOneById(id2), Duration("10 seconds"))
    optionUser2 must not be beNone
    optionUser2.get.accounts.size must beEqualTo(2)
    optionUser2.get.accounts(0).id must beEqualTo(id)
    optionUser2.get.accounts(1).id must beEqualTo(id2)
    //Delete user
    Await.result(UserDao.delete(optionUser2.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 3 : add, addColumn, find and delete
   */
  def test3(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(user, column), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns must beSome
    optionUser3.get.columns.get.size must beEqualTo(1)
    optionUser3.get.columns.get(0).title must beEqualTo("test")
    optionUser3.get.columns.get(0).unifiedRequests.size must beEqualTo(1)
    optionUser3.get.columns.get(0).unifiedRequests(0).service must beEqualTo("test")
    optionUser3.get.columns.get(0).unifiedRequests(0).args must beSome
    optionUser3.get.columns.get(0).unifiedRequests(0).args.get.size must beEqualTo(1)
    optionUser3.get.columns.get(0).unifiedRequests(0).args.get("t") must beEqualTo("t")
    //Delete user
    Await.result(UserDao.delete(optionUser3.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }
  
  /**
   * Test 4 : add, delColumn, find and delete
   */
  def test4(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(user, column), Duration("10 seconds"))
    //modif user
    Await.result(UserDao.deleteColumn(user, "test"), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns must beSome
    optionUser3.get.columns.get.size must beEqualTo(0)
    //Delete user
    Await.result(UserDao.delete(optionUser3.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }
  
  /**
   * Test 5 : add, modColumn, find and delete
   */
  def test5(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(user, column), Duration("10 seconds"))
    //modif user
    val column2 = UtilTest.makeColumn("test2", "test2", "t2", "t2")
    Await.result(UserDao.updateColumn(user, "test", column2), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns must beSome
    optionUser3.get.columns.get.size must beEqualTo(1)
    optionUser3.get.columns.get(0).title must beEqualTo("test2")
    optionUser3.get.columns.get(0).unifiedRequests.size must beEqualTo(1)
    optionUser3.get.columns.get(0).unifiedRequests(0).service must beEqualTo("test2")
    optionUser3.get.columns.get(0).unifiedRequests(0).args must beSome
    optionUser3.get.columns.get(0).unifiedRequests(0).args.get.size must beEqualTo(1)
    optionUser3.get.columns.get(0).unifiedRequests(0).args.get("t2") must beEqualTo("t2")
    //Delete user
    Await.result(UserDao.delete(optionUser3.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }
  
  /**
   * Test 6 : add, addProvider, findbyProvider and delete
   */
  def test6(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val provider = ProviderUser("p1", "p1", None, Some("p1"), Some("p1"), Some("p1"), Some("p1"))
    Await.result(UserDao.addProviderUser(user, provider), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findByIdProvider("p1", "p1"), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.distants must beSome
    optionUser3.get.distants.get.size must beEqualTo(1)
    optionUser3.get.distants.get(0).id must beEqualTo("p1")
    optionUser3.get.distants.get(0).socialType must beEqualTo("p1")
    // we don't save personal data
    optionUser3.get.distants.get(0).username must beNone
    optionUser3.get.distants.get(0).name must beNone
    optionUser3.get.distants.get(0).description must beNone
    optionUser3.get.distants.get(0).avatar must beNone
    //Delete user
    Await.result(UserDao.delete(optionUser3.get), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

}
