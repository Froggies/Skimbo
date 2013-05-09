package services.dao

import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.api.Play.current
import models.command.DelayedPost
import scala.concurrent.Future
import reactivemongo.bson.BSONDocument
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object DelayedPostDao {

  import play.api.libs.concurrent.Execution.Implicits._
  import models.User._

  val db = ReactiveMongoPlugin.db
  val collection = db("delayedPosts")

  def add(post: DelayedPost) = {
    collection.insert(post)
  }
  
  def get(delayInMinutes: Int): Future[Seq[DelayedPost]] = {
    val time = new DateTime(DateTimeZone.UTC)
    val end = time.plusMinutes(delayInMinutes).getMillis()
    val query = 
      BSONDocument("timeToPost" -> BSONDocument("$lte" -> end))
    collection.find(query).cursor[DelayedPost].toList
  }
  
  def delete(post: DelayedPost) = {
    collection.remove(post)
  }
  
}