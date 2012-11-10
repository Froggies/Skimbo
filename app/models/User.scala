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
  columns: Option[Seq[Column]] = None)

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
  
case class Column(
  title: String,
  unifiedRequests:Seq[UnifiedRequest])

object User {

  def create(id: String): User = {
    User(Seq[Account](Account(id, new Date())))
  }

  def toJson(user: User): JsValue = {
    val accounts = user.accounts
    val distants = user.distants.getOrElse { Seq() }
    val columns = user.columns.getOrElse { Seq() }
    JsObject(Seq(
      "accounts" -> JsArray(toJsonA(accounts)),
      "distants" -> JsArray(toJsonPU(distants)),
      "columns" -> JsArray(toJsonC(columns))))
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

  def toJsonC(columns: Seq[Column]): Seq[JsObject] = {
    columns.map { column =>
      val unifiedRequests = toJsonU(column.unifiedRequests)
      JsObject(
        Seq(
          "title" -> JsString(column.title),
          "unifiedRequests" -> JsArray(unifiedRequests)))
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
      val columns = tableTo[Column](document, "columns", { c =>
        val unifiedRequests = tableTo[UnifiedRequest](document, "unifiedRequests", { r =>
          val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable.bsonIterator
          val args = for (requestArg <- requestArgs) yield {
            (requestArg.name, r.getAs[BSONDocument]("args").get.toTraversable.getAs[BSONString](requestArg.name).get.value)
          }
          UnifiedRequest(
            asString(r, "service"),
            Some(args.toMap))
        })
        Column(asString(c, "title"), unifiedRequests)
      })
      User(
        accounts,
        Some(providers),
        Some(columns))
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

      val columns = toArray[Column](user.columns.getOrElse(Seq()), { column =>
        val unifiedRequests = BSONArray().toAppendable
        for (unifiedRequest <- column.unifiedRequests) yield {
          val args = BSONDocument().toAppendable
          for ((argKey, argValue) <- unifiedRequest.args.getOrElse(Map.empty)) yield {
            args.append(argKey -> BSONString(argValue))
          }
          unifiedRequests.append(BSONDocument(
            "service" -> BSONString(unifiedRequest.service),
            "args" -> args))
        }
        BSONDocument(
            "title" -> BSONString(column.title),
            "unifiedRequests" -> unifiedRequests)
      })

      BSONDocument(
        "accounts" -> accounts,
        "distants" -> distants,
        "columns" -> columns)
    }
  }

}