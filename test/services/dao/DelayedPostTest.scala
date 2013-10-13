package services.dao

import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import models.command.DelayedPost
import models.Post
import models.command.PostDelayedProvider
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DelayedPostTest extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimboTest")

  def createDelayedPost(min: Int, isMinus: Boolean = false) = {
    val date =
      if(isMinus) {
        new DateTime(DateTimeZone.UTC).minusMinutes(min).getMillis()
      } else {
        new DateTime(DateTimeZone.UTC).plusMinutes(min).getMillis()
      }
    DelayedPost(
        "test",
        Post(
            "titlePost",
            "messagePost",
            Some("skimbo.fr")
        ),
        Seq(PostDelayedProvider("nameProvider", None)),
        date
    )
  }
  
  "DelayedPostDao" should {
    "Add, get a delayedPost" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {
        
        Await.result(DelayedPostDao.add(createDelayedPost(600, true)), Duration("10 seconds"))
        Await.result(DelayedPostDao.add(createDelayedPost(0)), Duration("10 seconds"))
        Await.result(DelayedPostDao.add(createDelayedPost(20)), Duration("10 seconds"))
        val posts = Await.result(DelayedPostDao.get(15), Duration("10 seconds"))
        posts.length must beEqualTo(2)
        posts.foreach(p => Await.result(DelayedPostDao.delete(p), Duration("10 seconds")))
        val posts2 = Await.result(DelayedPostDao.get(20), Duration("10 seconds"))
        posts2.length must beEqualTo(1)
        posts2.foreach(DelayedPostDao.delete(_))
        Await.result(DelayedPostDao.get(30), Duration("10 seconds")).length must beEqualTo(0)
      }
    }
  }

}