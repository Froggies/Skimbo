package controllers;

import play.api._
import play.api.mvc._

// Reactive Mongo imports
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.core.commands._
import scala.concurrent.ExecutionContext

// Reactive Mongo plugin
import play.modules.reactivemongo._
import play.modules.reactivemongo.PlayBsonImplicits._

// Play Json imports
import play.api.libs.json._

import play.api.Play.current

object User extends Controller with MongoController {

  val db = ReactiveMongoPlugin.db
  lazy val collection = db("users")

  def add(email: String) = Action {
  	Async {
	    val json = Json.obj(
	      "email" -> email, 
	      "created" -> new java.util.Date().getTime()
	    )

	    collection.insert[JsValue]( json ).map( lastError =>
	      Ok("Mongo LastErorr : %s".format(lastError))
	    )
    }
  }

  def findAll() = Action {
	  Async {
		  val qb = QueryBuilder().query(Json.obj()).sort( "created" -> SortOrder.Descending)

		  collection.find[JsValue]( qb ).toList.map { collection =>
		    Ok(collection.foldLeft(JsArray(List()))( (obj, u) => obj ++ Json.arr(u) ))
		  }
	  } 
  }

  def findOneByEmail(email:String) = Action {
  	Async {
	    val qb = QueryBuilder().query(Json.obj("email" -> email)).sort( "created" -> SortOrder.Descending)

	    collection.find[JsValue]( qb ).toList.map { collection =>
	      Ok(collection.foldLeft(JsArray(List()))( (obj, u) => obj ++ Json.arr(u) ))
	    }
  	}
	} 

}