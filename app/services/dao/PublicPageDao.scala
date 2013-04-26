package services.dao

import scala.concurrent.Future
import models.PublicPage.PublicPageBSONReader
import models.PublicPage.PublicPageBSONWriter
import models.user.Column
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONString
import models.user.ProviderUser

object PublicPageDao {

  import play.api.libs.concurrent.Execution.Implicits._

  val db = ReactiveMongoPlugin.db
  val collection = db("publicPage")

  def add(publicPage: models.PublicPage) = {
    collection.insert(publicPage)
  }

  def findOneByName(name: String): Future[Option[models.PublicPage]] = {
    val query = BSONDocument("name" -> new BSONString(name))
    collection.find(query).cursor[models.PublicPage].headOption()
  }

  def delete(name: String) = {
    val query = BSONDocument("name" -> new BSONString(name))
    collection.remove(query)
  }

  def addColumn(name: String, column: models.user.Column) = {
    val query = BSONDocument("name" -> new BSONString(name))
    val update = BSONDocument("$push" -> BSONDocument("columns" -> models.user.Column.toBSON(column)))
    collection.update(query, update)
  }

  def updateColumn(name: String, title: String, column: Column) = {
    val query = BSONDocument(
      "name" -> new BSONString(name),
      "columns.title" -> new BSONString(title))
    val update = BSONDocument("$set" -> BSONDocument("columns.$" -> Column.toBSON(column)))
    collection.update(query, update)
  }

  def deleteColumn(name: String, columnTitle: String) = {
    val query = BSONDocument("name" -> new BSONString(name))
    val update = BSONDocument("$pull" -> BSONDocument("columns" -> BSONDocument("title" -> new BSONString(columnTitle))))
    collection.update(query, update)
  }
  
  def addDistant(name: String, distant: ProviderUser) = {
    val query = BSONDocument("name" -> new BSONString(name))
    val update = BSONDocument("$push" -> BSONDocument("distants" -> models.user.ProviderUser.toBSON(distant)))
    collection.update(query, update)
  }
  
  def deleteDistant(name: String, idDistant: String) = {
    val query = BSONDocument("name" -> new BSONString(name))
    val update = BSONDocument("$pull" -> BSONDocument("distants" -> BSONDocument("id" -> new BSONString(idDistant))))
    collection.update(query, update)
  }

}