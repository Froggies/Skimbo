package controllers;

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import models.user.Column
import services.endpoints.JsonRequest._

import play.modules.reactivemongo._
import scala.concurrent.{ExecutionContext, Future}

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler

object UserDao {

  import play.api.libs.concurrent.Execution.Implicits._
  import models.User._
  
  val db = ReactiveMongoPlugin.db
  val collection = db("users")

  def add(user: models.User) = {
    collection.insert(user)
  }

  def findAll(): Future[List[models.User]] = {
    val query = BSONDocument()
    collection.find(query).toList
  }

  def findOneById(id: String): Future[Option[models.User]] = {
    val query = BSONDocument("accounts.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def update(user: models.User) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    collection.update(query, user)
  }
  
  def updateColumn(user: models.User, title:String, column:Column) = {
    deleteColumn(user, title)
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$push" -> BSONDocument("columns" -> Column.toBSON(column)))
    collection.update(query, update)
  }

  def findByIdProvider(provider: String, id: String): Future[Option[models.User]] = {
    val query = BSONDocument(
      "distants.social" -> new BSONString(provider),
      "distants.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def deleteColumn(user: models.User, columnTitle: String) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$pull" -> BSONDocument("columns" -> BSONDocument("title" -> new BSONString(columnTitle))))
    collection.update(query, update)
  }
  
  def delete(user: models.User) = {
    collection.remove(user)
  }

}