package controllers;

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.iteratee.{Iteratee, Enumerator}
import models.User
import play.api.libs.concurrent.futureToPlayPromise
import play.modules.reactivemongo._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import models.ProviderUser
import reactivemongo.core.protocol.Query
import models.Column
import services.endpoints.JsonRequest._

object UserDao {

  val db = ReactiveMongoPlugin.db
  val collection = db("users2")

  def add(user: models.User)(implicit context:scala.concurrent.ExecutionContext) = {
    implicit val writer = User.UserBSONWriter
    collection.insert(user)
  }

  def findAll()(implicit context:scala.concurrent.ExecutionContext):Future[List[User]] = {
    implicit val reader = User.UserBSONReader
    val query = BSONDocument()
    val found = collection.find(query)
    found.toList
  }

  def findOneById(id: String)(implicit context:scala.concurrent.ExecutionContext):Future[Option[User]] = {
    implicit val reader = User.UserBSONReader
    val query = BSONDocument("accounts.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def update(user:models.User)(implicit context:scala.concurrent.ExecutionContext) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    collection.update(query, user)
  }

  def findByIdProvider(provider:String, id:String):Future[Option[User]] = {
    implicit val reader = User.UserBSONReader
    import scala.concurrent.ExecutionContext.Implicits.global
    val query = BSONDocument(
      "distants.social" -> new BSONString(provider),
      "distants.id" -> new BSONString(id))
    collection.find(query).headOption()
  }
  
  def deleteColumn(user:models.User, columnTitle:String)(implicit context:scala.concurrent.ExecutionContext) = {
    val index = user.columns.getOrElse(Seq[Column]()).indexOf(Column(columnTitle, Seq[UnifiedRequest]()))
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$unset" -> BSONDocument("columns."+index -> new BSONInteger(1)))
    collection.update(query, update)
  }

}