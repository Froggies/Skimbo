package controllers;

import play.api._
import play.api.mvc._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.core.commands._
import scala.concurrent.ExecutionContext
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._
import play.api.libs.json._
import play.api.Play.current

object UserDao extends Controller with MongoController {

  val db = ReactiveMongoPlugin.db
  lazy val collection = db("users")

  def add(user:models.User) = {
    val json = models.User.toJson(user)
    collection.insert[JsValue](json).map(lastError =>
      Logger.error("Mongo LastErorr : %s".format(lastError)))
  }

  def findAll() = {
    val qb = QueryBuilder().query(Json.obj())
    collection.find[JsValue](qb).toList.map { collection =>
      models.User.fromJson(collection.foldLeft(JsArray(List()))((obj, u) => obj ++ Json.arr(u)))
    }
  }
  
  def findOneById(id: String) = {
    val qb = QueryBuilder().query(Json.obj("id" -> id))
    collection.find[JsValue](qb).toList.map { collection =>
      models.User.fromJson(collection.foldLeft(JsArray(List()))((obj, u) => obj ++ Json.arr(u)))
    }
  }

}