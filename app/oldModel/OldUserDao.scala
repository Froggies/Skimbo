package oldModel

import scala.concurrent.Future

import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument

object OldUserDao {

  import play.api.libs.concurrent.Execution.Implicits._
  import models.User._

  def db = ReactiveMongoPlugin.db
  def collection = db[BSONCollection]("users")

  def findAll(): Future[List[OldUser]] = {
    val query = BSONDocument()
    collection.find(query).cursor[OldUser].toList
  }

}