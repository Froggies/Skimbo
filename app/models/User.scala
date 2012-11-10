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
import reactivemongo.bson.handlers.{ BSONReader, BSONWriter }
import java.util.Date

case class User(
  accounts: Seq[Account],
  distants: Option[Seq[ProviderUser]] = None,
  unifiedRequests: Option[Seq[UnifiedRequest]] = None)

case class Account(
  id: String,
  lastUse: Date)

//keep has Option rules username, name, desctription and avatar for condition's providers
case class ProviderUser(
  id: String,
  username: Option[String] = None,
  name: Option[String] = None,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {

  def create(id: String): User = {
    User(Seq[Account](Account(id, new Date())))
  }

  def fromJson(json: JsValue): Option[User] = {
    val jsonAccounts: Seq[JsValue] = (json \ "accounts").as[Seq[JsValue]]
    val accounts = jsonAccounts.map { account =>
      Account((account \ "id").as[String],
        (account \ "lastUse").as[Date])
    }.toList
    val jsonDistants: Option[Seq[JsValue]] = (json \ "distants").asOpt[Seq[JsValue]]
    val distants = jsonDistants.map { d =>
      for (distant <- d) yield {
        ProviderUser((distant \ "id").as[String],
          (distant \ "login").asOpt[String],
          (distant \ "name").asOpt[String],
          (distant \ "social").as[String],
          (distant \ "desc").asOpt[String],
          (distant \ "avatar").asOpt[String])
      }
    }
    Some(User(accounts, distants))
  }

  def toJson(user: User): JsValue = {
    val accounts = user.accounts
    val distants = user.distants.getOrElse { Seq() }
    val unifiedRequests = user.unifiedRequests.getOrElse { Seq() }
    JsObject(Seq(
      "accounts" -> JsArray(toJsonA(accounts)),
      "distants" -> JsArray(toJsonPU(distants)),
      "unifiedRequests" -> JsArray(toJsonU(unifiedRequests))))
  }

  def toJsonA(accounts: Seq[Account]): Seq[JsObject] = {
    accounts.map { account =>
      JsObject(
        Seq(
          "id" -> JsString(account.id),
          "lastUse" -> JsString(account.lastUse.getTime().toString())))
    }
  }

  def toJsonPU(distants: Seq[ProviderUser]): Seq[JsObject] = {
    distants.map { distant =>
      JsObject(
        Seq(
          "id" -> JsString(distant.id),
          "login" -> JsString(distant.username.getOrElse("")),
          "name" -> JsString(distant.name.getOrElse("")),
          "social" -> JsString(distant.socialType),
          "desc" -> JsString(distant.description.getOrElse("")),
          "avatar" -> JsString(distant.avatar.getOrElse(""))))
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
          "args" -> JsObject(args.toList)))
    }
  }

  /**
   * From bd, keep comment for condition's providers
   */
  implicit object UserBSONReader extends BSONReader[User] {
    def tableTo[Obj](document: BSONDocument, key:String, transform:(TraversableBSONDocument) => Obj):Seq[Obj] = {
      val doc = document.toTraversable
      val objs = doc.getAs[BSONArray](key).getOrElse(BSONArray()).toTraversable.bsonIterator
      val seqObjs = for (obj <- objs) yield {
        val a = obj.value.asInstanceOf[BSONDocument].toTraversable
        transform(a)
      }
      seqObjs.toList
    }
    
    def asString(doc:TraversableBSONDocument, key:String):String = {
      doc.getAs[BSONString](key).get.value
    }
    
    def fromBSON(document: BSONDocument): User = {
      val accounts = tableTo[Account](document, "accounts", { a =>
        val lastUse = new Date()
        lastUse.setTime(a.getAs[BSONDateTime]("lastUse").get.value)
        Account(
          asString(a, "id"),
          lastUse)
      })
      val providers = tableTo[ProviderUser](document, "accounts", { d =>
        ProviderUser(
          asString(d, "id"),
          None, //d.getAs[BSONString]("login").get.value,
          None, //d.getAs[BSONString]("name").get.value,
          asString(d, "social"),
          None, //Some(d.getAs[BSONString]("desc").get.value),
          None //Some(d.getAs[BSONString]("avatar").get.value)
          )
      })
      val unifiedRequests = tableTo[UnifiedRequest](document, "unifiedRequests", { r =>
        val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable.bsonIterator
        val args = for (requestArg <- requestArgs) yield {
          (requestArg.name, r.getAs[BSONDocument]("args").get.toTraversable.getAs[BSONString](requestArg.name).get.value)
        }
        UnifiedRequest(
          asString(r, "service"),
          Some(args.toMap))
      })
      User(
        accounts,
        Some(providers),
        Some(unifiedRequests))
    }
  }

  /**
   * To bd, keep comment for condition's providers
   */
  implicit object UserBSONWriter extends BSONWriter[User] {
    def toArray[Obj](objs:Seq[Obj], transform:(Obj) => BSONDocument):BSONArray = {
      val res = BSONArray().toAppendable
      for (obj <- objs) yield {
        res.append(transform(obj))
      }
      res
    }
    
    def toBSON(user: User) = {
      val accounts = toArray[Account](user.accounts, { account =>
        BSONDocument( 
          "id" -> BSONString(account.id),
          "lastUse" -> BSONDateTime(account.lastUse.getTime()))
      })

      val distants = toArray[ProviderUser](user.distants.getOrElse(Seq()), { distant =>
        BSONDocument(
          "id" -> BSONString(distant.id),
          "social" -> BSONString(distant.socialType))
      })

      val unifiedRequests = BSONArray().toAppendable
      for (unifiedRequest <- user.unifiedRequests.getOrElse(Seq())) yield {
        val args = BSONDocument().toAppendable
        for ((argKey, argValue) <- unifiedRequest.args.getOrElse(Map.empty)) yield {
          args.append(argKey -> BSONString(argValue))
        }
        unifiedRequests.append(BSONDocument(
          "service" -> BSONString(unifiedRequest.service),
          "args" -> args))
      }

      BSONDocument(
        "accounts" -> accounts,
        "distants" -> distants,
        "unifiedRequests" -> unifiedRequests)
    }
  }

}