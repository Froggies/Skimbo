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

object UserDao {

  val db = ReactiveMongoPlugin.db
  val collection = db("users2")
    
  def add(user: models.User)(implicit context:scala.concurrent.ExecutionContext):Future[User] = {
    implicit val writer = User.UserBSONWriter
    collection.insert(user)
    findOneById(user.id).map { user =>
      user.getOrElse { 
        throw new UnexpectedException(Some("Add user fail"))
      }
    }
  }

  def findAll()(implicit context:scala.concurrent.ExecutionContext):Future[List[User]] = {
    implicit val reader = User.UserBSONReader
    val query = BSONDocument()
    val found = collection.find(query)
    found.toList
  }

  def findOneById(id: String)(implicit context:scala.concurrent.ExecutionContext):Future[Option[User]] = {
    implicit val reader = User.UserBSONReader
    val query = BSONDocument("id" -> new BSONString(id))
    collection.find(query).headOption()
  }
  
  def findOrCreate(id: String)(implicit context:scala.concurrent.ExecutionContext):Future[User] = {
    findOneById(id).map { user =>
      user.getOrElse { 
        return add(User(id))
      }
    }
  }
  
  def update(user:models.User)(implicit context:scala.concurrent.ExecutionContext) = {
    collection.update(BSONDocument("id" -> new BSONString(user.id)), user)
  }

}