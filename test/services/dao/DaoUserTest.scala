package services.dao

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.joda.time.DateTime
import org.specs2.mutable.Specification

import models.ParamHelper
import models.User
import models.user.Account
import models.user.Column
import models.user.OptionUser
import models.user.ProviderUser
import models.user.ServiceArg
import models.user.SinceId
import models.user.SkimboToken
import play.api.test.FakeApplication
import play.api.test.Helpers.running
import services.auth.providers.Twitter

object UtilTest {

  def makeUser(id: String) = {
    User.create(id)
  }

  def makeAccount(id: String) = {
    Account.create(id)
  }

  def makeAccountOld(id: String) = {
    Account(id, new DateTime().minusDays(1).toDate(), "", "Skimbo")
  }

  def makeColumn(title: String, urService: String, urArg1: String, urArg2: String) = {
    Column(
        title, 
        Seq(
            models.user.UnifiedRequest(
                urService, 
                Seq(
                    ServiceArg(
                        urArg1, 
                        ParamHelper(urArg2, urArg2, "", None))),
                "",
                Seq(SinceId("", "")))), 
        0, -1, -1)
  }

  def makeFullUser(id: String) = {
    User(
      OptionUser.create(),
      Seq(makeAccount(id)),
      Seq(ProviderUser("provider_" + id, "provider_" + id, None)),
      Seq(makeColumn("col_" + id, "col_" + id, "col_" + id, "col_" + id)))
  }

}

class DaoUserSimpleFind extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  "DaoUser" should {
    "Add, find and delete a user" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {

        val id = "test"
        val user = UtilTest.makeUser(id)

        addUser(id, user)
        modifUser(id, user)
        addColumn(id, user)
        delColumn(id, user)
        modColumn(id, user)
        addAndFindProvider(id, user)
        updateLastUse(id, user)
        mergeUser

        Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
      }
    }
  }

  /**
   * Test 1 : add, find and delete
   */
  def addUser(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //Find user
    val optionUser: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser must not be beNone
    optionUser.get.accounts.size must beEqualTo(1)
    optionUser.get.accounts(0).id must beEqualTo(id)
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 2 : add, mod, find and delete
   */
  def modifUser(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val id2 = "test2.1"
    Await.result(UserDao.addAccount(id, UtilTest.makeAccount(id2)), Duration("10 seconds"))
    //Find user
    val optionUser2: Option[User] = Await.result(UserDao.findOneById(id2), Duration("10 seconds"))
    optionUser2 must not be beNone
    optionUser2.get.accounts.size must beEqualTo(2)
    optionUser2.get.accounts(0).id must beEqualTo(id)
    optionUser2.get.accounts(1).id must beEqualTo(id2)
    //Delete user
    Await.result(UserDao.delete(id2), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 3 : add, addColumn, find and delete
   */
  def addColumn(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(id, column), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns.size must beEqualTo(1)
    optionUser3.get.columns(0).title must beEqualTo("test")
    optionUser3.get.columns(0).unifiedRequests.size must beEqualTo(1)
    optionUser3.get.columns(0).unifiedRequests(0).service must beEqualTo("test")
    optionUser3.get.columns(0).unifiedRequests(0).args.size must beEqualTo(1)
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 4 : add, delColumn, find and delete
   */
  def delColumn(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(id, column), Duration("10 seconds"))
    //modif user
    Await.result(UserDao.deleteColumn(id, "test"), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns.size must beEqualTo(0)
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 5 : add, modColumn, find and delete
   */
  def modColumn(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val column = UtilTest.makeColumn("test", "test", "t", "t")
    Await.result(UserDao.addColumn(id, column), Duration("10 seconds"))
    Await.result(UserDao.addColumn(id, UtilTest.makeColumn("other", "other", "o", "o")), Duration("10 seconds"))
    //modif user
    val column2 = UtilTest.makeColumn("test2", "test2", "t2", "t2")
    Await.result(UserDao.updateColumn(id, "test", column2), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.columns.size must beEqualTo(2)
    optionUser3.get.columns(0).title must beEqualTo("test2")
    optionUser3.get.columns(0).unifiedRequests.size must beEqualTo(1)
    optionUser3.get.columns(0).unifiedRequests(0).service must beEqualTo("test2")
    optionUser3.get.columns(0).unifiedRequests(0).args.size must beEqualTo(1)
    optionUser3.get.columns(0).unifiedRequests(0).args(0).value.call must beEqualTo("t2")
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 6 : add, addProvider, findbyProvider and delete
   */
  def addAndFindProvider(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val token = SkimboToken("1")
    val provider = ProviderUser("p1", Twitter.name, Some(token), Some("p1"), Some("p1"), Some("p1"), Some("p1"))
    Await.result(UserDao.setToken(id, Twitter, token), Duration("10 seconds"))
    Await.result(Await.result(UserDao.addProviderUser(id, provider), Duration("10 seconds")).get, Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findByIdProvider(Twitter.name, "p1"), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.distants.size must beEqualTo(1)
    optionUser3.get.distants(0).id must beEqualTo("p1")
    optionUser3.get.distants(0).socialType must beEqualTo(Twitter.name)
    // we don't save personal data
    optionUser3.get.distants(0).username must beNone
    optionUser3.get.distants(0).name must beNone
    optionUser3.get.distants(0).description must beNone
    optionUser3.get.distants(0).avatar must beNone
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 7 : update lastUse
   */
  def updateLastUse(id: String, user: User) {
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    //modif user
    val account = UtilTest.makeAccountOld("test2")
    Await.result(UserDao.addAccount(id, account), Duration("10 seconds"))
    Await.result(UserDao.updateLastUse("test2"), Duration("10 seconds"))
    //Find user
    val optionUser3: Option[User] = Await.result(UserDao.findOneById(id), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.accounts.size must beEqualTo(2)
    optionUser3.get.accounts(1).id must beEqualTo("test2")
    optionUser3.get.accounts(1).lastUse must not be equalTo(account.lastUse)
    //Delete user
    Await.result(UserDao.delete(id), Duration("10 seconds"))
    Await.result(UserDao.findOneById(id), Duration("10 seconds")) must be beNone
  }

  /**
   * Test 8
   */
  def mergeUser() {
    val user = UtilTest.makeFullUser("test")
    val user2 = UtilTest.makeFullUser("test2")
    val user3 = UtilTest.makeFullUser("test3")
    //Add user
    Await.result(UserDao.add(user), Duration("10 seconds"))
    Await.result(UserDao.add(user2), Duration("10 seconds"))
    Await.result(UserDao.add(user3), Duration("10 seconds"))
    //modif user
    Await.result(
      Await.result(
        UserDao.merge("test", "test2"),
        Duration("10 seconds")).get,
      Duration("10 seconds")).get
    Thread.sleep(500)
    //Find user
    val optionUser: Option[User] = Await.result(UserDao.findOneById("test"), Duration("10 seconds"))
    optionUser must beSome
    optionUser.get.accounts.size must beEqualTo(2)
    optionUser.get.accounts(1).id must beEqualTo("test2")
    val optionUser3: Option[User] = Await.result(UserDao.findOneById("test3"), Duration("10 seconds"))
    optionUser3 must beSome
    optionUser3.get.accounts.size must beEqualTo(1)
    //Delete user
    Await.result(UserDao.delete("test"), Duration("10 seconds"))
    Await.result(UserDao.findOneById("test2"), Duration("10 seconds")) must beNone
    Await.result(UserDao.delete("test3"), Duration("10 seconds"))
    Await.result(UserDao.findOneById("test3"), Duration("10 seconds")) must be beNone
  }

}
