package models

import java.util.Date
import models.user.Column
import models.user.ProviderUser
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONDocumentReader

case class User(
  accounts: Seq[models.user.Account],
  distants: Option[Seq[ProviderUser]] = None,
  columns: Option[Seq[Column]] = None)

object User {

  def create(id: String): User = {
    User(Seq[models.user.Account](models.user.Account(id, new Date())))
  }

  def toJson(user: User): JsValue = {
    Json.obj(
      "accounts" -> Json.toJson(user.accounts),
      "distants" -> Json.toJson(user.distants.getOrElse(Seq.empty)),
      "columns" -> Json.toJson(user.columns.getOrElse(Seq.empty)))
  }

  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(document: BSONDocument): User = {
      val accounts = UtilBson.tableTo[models.user.Account](document, "accounts", { a =>
        models.user.Account.fromBSON(a)
      })
      val providers = UtilBson.tableTo[ProviderUser](document, "distants", { d =>
        ProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[Column](document, "columns", { c =>
        Column.fromBSON(c)
      })
      User(accounts, Some(providers), Some(columns))
    }
  }

  def toBSON(user: User) = {
    val accounts = UtilBson.toArray[models.user.Account](user.accounts, { account =>
      models.user.Account.toBSON(account)
    })

    val distants = UtilBson.toArray[ProviderUser](user.distants.getOrElse(Seq()), { distant =>
      ProviderUser.toBSON(distant)
    })

    val columns = UtilBson.toArray[Column](user.columns.getOrElse(Seq()), { column =>
      Column.toBSON(column)
    })

    BSONDocument(
      "accounts" -> accounts,
      "distants" -> distants,
      "columns" -> columns)
  }

}