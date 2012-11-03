package models
import play.api.libs.json.JsValue
import controllers.UserDao
import services.actors.UserInfosActor
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import scala.collection.Map
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import play.api.libs.json.JsNull
import reactivemongo.bson.handlers.BSONReader
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONArray
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONString
import reactivemongo.bson.handlers.BSONWriter
import reactivemongo.bson.BSONIterator
import play.api.libs.iteratee.Iteratee$
import play.api.libs.iteratee.Iteratee
import reactivemongo.bson.DefaultBSONIterator
import reactivemongo.bson.BSONElement
import reactivemongo.bson.BSONStructure
import reactivemongo.bson.BSONValue

case class User(
  id: String,
  distants: Option[Seq[ProviderUser]] = None)

case class ProviderUser(
  id: String,
  username: String,
  name: String,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {
  def load(id: String): Option[User] = {
    Some(User(id))
  }

  def fromJson(json: JsValue): Option[User] = {
    val id: String = (json \ "id").as[String]
    val distants: Option[Seq[JsValue]] = (json \ "distants").asOpt[Seq[JsValue]]
    distants.flatMap { d =>
      val transform = for (distant <- d) yield {
        ProviderUser((distant \ "id").as[String],
          (distant \ "login").as[String],
          (distant \ "name").as[String],
          (distant \ "social").as[String],
          (distant \ "desc").asOpt[String],
          (distant \ "avatar").asOpt[String])
      }
      Some(User(id, Some(transform)))
    }
  }

  def toJson(user: User): JsValue = {
    val distants = user.distants.map { d => d }.getOrElse {Seq()}
    JsObject(Seq(
      "id" -> JsString(user.id),
      "distants" -> JsArray(toJson(distants))
    ))
  }
  
  def toJson(distants: Seq[ProviderUser]): Seq[JsObject] = {
    distants.map { distant =>
     JsObject(
       Seq(
         "id" -> JsString(distant.id),
         "login" -> JsString(distant.username),
         "name" -> JsString(distant.name),
         "social" -> JsString(distant.socialType),
         "desc" -> JsString(distant.description.getOrElse("")),
         "avatar" -> JsString(distant.avatar.getOrElse(""))
       )
     )
    }
  }
  
  implicit object UserBSONReader extends BSONReader[User] {
    def fromBSON(document: BSONDocument) :User = {
      val doc = document.toTraversable
      val distants = doc.getAs[BSONArray]("distants").getOrElse(BSONArray()).toTraversable.bsonIterator
      val seqProviders = for(dist <- distants) yield {
        val d = dist.value.asInstanceOf[BSONDocument].toTraversable
        ProviderUser(
          d.getAs[BSONString]("id").get.value,
          d.getAs[BSONString]("login").get.value,
          d.getAs[BSONString]("name").get.value,
          d.getAs[BSONString]("social").get.value,
          Some(d.getAs[BSONString]("desc").get.value),
          Some(d.getAs[BSONString]("avatar").get.value)
        )
      }
      User(
        doc.getAs[BSONString]("id").get.value,
        Some(seqProviders.toList)
      )
    }
  }

  implicit object UserBSONWriter extends BSONWriter[User] {
    def toBSON(user: User) = {
      val distants = BSONArray().toAppendable
      for(distant <- user.distants.getOrElse(Seq())) yield {
        distants.append(BSONDocument(
          "id" -> BSONString(distant.id),
          "login" -> BSONString(distant.username),
          "name" -> BSONString(distant.name),
          "social" -> BSONString(distant.socialType),
          "desc" -> BSONString(distant.description.getOrElse("")),
          "avatar" -> BSONString(distant.avatar.getOrElse(""))
        ))
      }
      
      BSONDocument(
        "id" -> BSONString(user.id),
        "distants" -> distants)
    }
  }

}