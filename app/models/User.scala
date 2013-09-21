package models

import java.util.Date

import models.user.Column
import models.user.OptionUser
import models.user.ProviderUser
import models.user.StatUser
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.Producer.nameValue2Producer
import services.dao.UtilBson

case class User(
  options: OptionUser,
  accounts: Seq[models.user.Account],
  distants: Seq[ProviderUser] = Seq.empty,
  columns: Seq[Column] = Seq.empty,
  stats: Seq[StatUser] = Seq.empty
)

object User {

  def create(id: String): User = {
    User(OptionUser.create, Seq[models.user.Account](models.user.Account.create(id)))
  }

  def toJson(user: User): JsValue = {
    Json.obj(
      "options" -> Json.toJson(user.options),
      "accounts" -> Json.toJson(user.accounts),
      "distants" -> Json.toJson(user.distants),
      "columns" -> Json.toJson(user.columns))
  }

  implicit object UserBSONReader extends BSONDocumentReader[User] {
    def read(document: BSONDocument): User = {
      val optionUser = OptionUser.fromBSON(document.getAs[BSONDocument]("options").get)
      val accounts = UtilBson.tableTo[models.user.Account](document, "accounts", { a =>
        models.user.Account.fromBSON(a)
      })
      val providers = UtilBson.tableTo[ProviderUser](document, "distants", { d =>
        ProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[Column](document, "columns", { c =>
        Column.fromBSON(c)
      })
      val stats = UtilBson.tableTo[StatUser](document, "stats", { c =>
        StatUser.fromBSON(c)
      })
      User(optionUser, accounts, providers, columns, stats)
    }
  }

  def toBSON(user: User) = {
    val accounts = UtilBson.toArray[models.user.Account](user.accounts, { account =>
      models.user.Account.toBSON(account)
    })
    val distants = UtilBson.toArray[ProviderUser](user.distants, { distant =>
      ProviderUser.toBSON(distant)
    })
    val columns = UtilBson.toArray[Column](user.columns, { column =>
      Column.toBSON(column)
    })
    val stats = UtilBson.toArray[StatUser](user.stats, { stat =>
      StatUser.toBSON(stat)
    })
    BSONDocument(
      "options" -> OptionUser.toBSON(user.options),
      "accounts" -> accounts,
      "distants" -> distants,
      "columns" -> columns,
      "stats" -> stats)
  }

}