package services.dao

import scala.concurrent.Future
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONString
import reactivemongo.api.collections.default.BSONCollection
import models.StatsApp._

object StatsAppDao {

  import play.api.libs.concurrent.Execution.Implicits._

  def db = ReactiveMongoPlugin.db
  def collection = db[BSONCollection]("stats")

  def add(statsApp: models.StatsApp) = {
    collection.insert(statsApp)
  }
  
  def last(): Future[Option[models.StatsApp]] = {
    collection.find(BSONDocument()).sort(BSONDocument("timestamp" -> -1)).one[models.StatsApp]
  }
  
  def get(timestamp:Long): Future[Option[models.StatsApp]] = {
    val query = BSONDocument("timestamp" -> timestamp)
    collection.find(query).cursor[models.StatsApp].headOption()
  }
  
  def allTimestamp(): Future[List[Long]] = {
    val query = BSONDocument()//TODO find a way to do this in mongodb instead in scala !!!
    collection.find(query).sort(BSONDocument("timestamp" -> -1)).cursor[models.StatsApp].toList.map { l =>
      l.map(_.timestamp)
    }
  }
  
}