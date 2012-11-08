package models

import controllers.UserDao
import play.api.libs.json._
import reactivemongo.bson._
import scala.collection.Map
import play.api.libs.json.JsValue
import services.actors.UserInfosActor
import services.endpoints.JsonRequest._
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import reactivemongo.bson.handlers.{BSONReader, BSONWriter}

case class User(
  id: String,
  distants: Option[Seq[ProviderUser]] = None,
  unifiedRequests: Option[Seq[UnifiedRequest]] = None)

//keep has Option rules username, name, desctription and avatar for condition's providers 
case class ProviderUser(
  id: String,
  username: Option[String] = None,
  name: Option[String] = None,
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
          (distant \ "login").asOpt[String],
          (distant \ "name").asOpt[String],
          (distant \ "social").as[String],
          (distant \ "desc").asOpt[String],
          (distant \ "avatar").asOpt[String])
      }
      Some(User(id, Some(transform)))
    }
  }

  def toJson(user: User): JsValue = {
    val distants = user.distants.map { d => d }.getOrElse {Seq()}
    val unifiedRequests = user.unifiedRequests.getOrElse {Seq()}
    JsObject(Seq(
      "id" -> JsString(user.id),
      "distants" -> JsArray(toJson(distants)),
      "unifiedRequests" -> JsArray(toJsonU(unifiedRequests))
    ))
  }
  
  def toJson(distants: Seq[ProviderUser]): Seq[JsObject] = {
    distants.map { distant =>
     JsObject(
       Seq(
         "id" -> JsString(distant.id),
         "login" -> JsString(distant.username.getOrElse("")),
         "name" -> JsString(distant.name.getOrElse("")),
         "social" -> JsString(distant.socialType),
         "desc" -> JsString(distant.description.getOrElse("")),
         "avatar" -> JsString(distant.avatar.getOrElse(""))
       )
     )
    }
  }
  
  def toJsonU(unifiedRequests: Seq[UnifiedRequest]): Seq[JsObject] = {
    unifiedRequests.map { unifiedRequest =>
      val args = unifiedRequest.args.getOrElse(Seq()).map { m => 
        m._1 -> JsString(m._2)
      }
      JsObject(
         Seq(
           "service" -> JsString(unifiedRequest.service),
           "args" -> JsObject(args.toList)
         )
      )
    }
  }
  
  /**
   * From bd, keep comment for condition's providers
   */
  implicit object UserBSONReader extends BSONReader[User] {
    def fromBSON(document: BSONDocument) :User = {
      val doc = document.toTraversable
      val distants = doc.getAs[BSONArray]("distants").getOrElse(BSONArray()).toTraversable.bsonIterator
      val seqProviders = for(dist <- distants) yield {
        val d = dist.value.asInstanceOf[BSONDocument].toTraversable
        ProviderUser(
          d.getAs[BSONString]("id").get.value,
          None,//d.getAs[BSONString]("login").get.value,
          None,//d.getAs[BSONString]("name").get.value,
          d.getAs[BSONString]("social").get.value,
          None,//Some(d.getAs[BSONString]("desc").get.value),
          None//Some(d.getAs[BSONString]("avatar").get.value)
        )
      }
      val unifiedRequests = doc.getAs[BSONArray]("unifiedRequests").getOrElse(BSONArray()).toTraversable.bsonIterator
      val seqUnifiedRequests = for(request <- unifiedRequests) yield {
        val r = request.value.asInstanceOf[BSONDocument].toTraversable
        val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable.bsonIterator
        val args = for(requestArg <- requestArgs) yield {
          (requestArg.name, r.getAs[BSONDocument]("args").get.toTraversable.getAs[BSONString](requestArg.name).get.value)
        }
        UnifiedRequest(
          r.getAs[BSONString]("service").get.value,
          Some(args.toMap)
        )
      }
      User(
        doc.getAs[BSONString]("id").get.value,
        Some(seqProviders.toList),
        Some(seqUnifiedRequests.toList)
      )
    }
  }

  /**
   * To bd, keep comment for condition's providers
   */
  implicit object UserBSONWriter extends BSONWriter[User] {
    def toBSON(user: User) = {
      val distants = BSONArray().toAppendable
      for(distant <- user.distants.getOrElse(Seq())) yield {
        distants.append(BSONDocument(
          "id" -> BSONString(distant.id),
          //"login" -> BSONString(distant.username),
          //"name" -> BSONString(distant.name),
          "social" -> BSONString(distant.socialType)
          //"desc" -> BSONString(distant.description.getOrElse("")),
          //"avatar" -> BSONString(distant.avatar.getOrElse(""))
        ))
      }
      
      val unifiedRequests = BSONArray().toAppendable
      for(unifiedRequest <- user.unifiedRequests.getOrElse(Seq())) yield {
        val args = BSONDocument().toAppendable
        for((argKey, argValue) <- unifiedRequest.args.getOrElse(Map.empty)) yield {
          args.append(argKey -> BSONString(argValue))
        }
        unifiedRequests.append(BSONDocument(
          "service" -> BSONString(unifiedRequest.service),
          "args" -> args
        ))
      }
      
      BSONDocument(
        "id" -> BSONString(user.id),
        "distants" -> distants,
        "unifiedRequests" -> unifiedRequests)
    }
  }

}