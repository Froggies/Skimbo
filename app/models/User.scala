package models

import controllers.UserDao
import play.api.libs.json._
import reactivemongo.bson._
import scala.collection.Map
import play.api.libs.json.JsValue
import services.actors.UserInfosActor
import services.endpoints.JsonRequest._
import play.api.libs.iteratee._
import reactivemongo.bson.handlers.{ BSONReader, BSONWriter }
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import java.util.Date
import play.api.Logger
import models.user._

case class User(
  accounts: Seq[Account],
  distants: Option[Seq[ProviderUser]] = None,
  columns: Option[Seq[Column]] = None)

object User {

  def create(id: String): User = {
    User(Seq[Account](Account(id, new Date())))
  }

  def toJson(user: User): JsValue = {
    Json.obj(
      "accounts" -> Json.toJson(user.accounts),
      "distants" -> Json.toJson(user.distants.getOrElse(Seq.empty)),
      "columns" -> Json.toJson(user.columns.getOrElse(Seq.empty)))
  }

  def tableTo[Obj](document: BSONDocument, key: String, transform: (TraversableBSONDocument) => Obj): Seq[Obj] = {
    val doc = document.toTraversable
    val objs = doc.getAs[BSONArray](key).getOrElse(BSONArray()).toTraversable.toList
    val seqObjs = for (obj <- objs) yield {
      val a = obj.asInstanceOf[BSONDocument].toTraversable
      transform(a)
    }
    seqObjs.toList
  }

  /**
   * From bd, keep comment for condition's providers
   */
  implicit object UserBSONReader extends BSONReader[User] {
    def asString(doc: TraversableBSONDocument, key: String): String = {
      doc.getAs[BSONString](key).get.value
    }

    def fromBSON(document: BSONDocument): User = {
      val accounts = tableTo[Account](document, "accounts", { a =>
        val lastUse = new Date()
        lastUse.setTime(a.getAs[BSONDateTime]("lastUse").get.value)
        Account(asString(a, "id"), lastUse)
      })
      val providers = tableTo[ProviderUser](document, "distants", { d =>
        ProviderUser(
          asString(d, "id"),
          asString(d, "social"),
          SkimboToken.fromBSON(d.getAs[BSONDocument]("token").get)
          )
      })
      val columns = tableTo[Column](document, "columns", { c =>
        val unifiedRequests = tableTo[UnifiedRequest](c, "unifiedRequests", { r =>
          val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable
          val args = requestArgs.mapped.map { requestArg =>
            (requestArg._1, requestArgs.getAs[BSONString](requestArg._1).get.value)
          }
          if (args.nonEmpty) {
            UnifiedRequest(asString(r, "service"), Some(args.toMap))
          } else {
            UnifiedRequest(asString(r, "service"), None)
          }
        })
        Column(asString(c, "title"), unifiedRequests)
      })
      User(accounts, Some(providers), Some(columns))
    }
  }

  def toArray[Obj](objs: Seq[Obj], transform: (Obj) => BSONDocument): BSONArray = {
    val array = objs.map(transform(_))
    BSONArray(array: _*)
  }

  def toBSON(user: User) = {
    val accounts = toArray[Account](user.accounts, { account =>
      Account.toBSON(account)
    })

    val distants = toArray[ProviderUser](user.distants.getOrElse(Seq()), { distant =>
      ProviderUser.toBSON(distant)
    })

    val columns = toArray[Column](user.columns.getOrElse(Seq()), { column =>
      Column.toBSON(column)
    })

    BSONDocument(
      "accounts" -> accounts,
      "distants" -> distants,
      "columns" -> columns)
  }

}