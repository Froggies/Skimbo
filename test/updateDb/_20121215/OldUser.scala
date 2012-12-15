package updateDb._20121215

import java.util.Date
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.handlers.BSONReader
import services.dao.UtilBson

case class OldUser(
  accounts: Seq[models.user.Account],
  distants: Option[Seq[models.user.ProviderUser]] = None,
  columns: Option[Seq[OldColumn]] = None)

object OldUser {

  implicit object UserBSONReader extends BSONReader[OldUser] {

    def fromBSON(document: BSONDocument): OldUser = {
      val accounts = UtilBson.tableTo[models.user.Account](document, "accounts", { a =>
        models.user.Account.fromBSON(a)
      })
      val providers = UtilBson.tableTo[models.user.ProviderUser](document, "distants", { d =>
        models.user.ProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[OldColumn](document, "columns", { c =>
        OldColumn.fromBSON(c)
      })
      OldUser(accounts, Some(providers), Some(columns))
    }
  }

  def toBSON(user: OldUser) = {
    val accounts = UtilBson.toArray[models.user.Account](user.accounts, { account =>
      models.user.Account.toBSON(account)
    })

    val distants = UtilBson.toArray[models.user.ProviderUser](user.distants.getOrElse(Seq()), { distant =>
      models.user.ProviderUser.toBSON(distant)
    })

    val columns = UtilBson.toArray[OldColumn](user.columns.getOrElse(Seq()), { column =>
      OldColumn.toBSON(column)
    })

    BSONDocument(
      "accounts" -> accounts,
      "distants" -> distants,
      "columns" -> columns)
  }

}