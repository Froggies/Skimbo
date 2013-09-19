package oldModel

import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONDocumentReader

case class OldUser(
  accounts: Seq[OldAccount],
  distants: Option[Seq[OldProviderUser]] = None,
  columns: Option[Seq[OldColumn]] = None
)

object OldUser {

  implicit object UserBSONReader extends BSONDocumentReader[OldUser] {
    def read(document: BSONDocument): OldUser = {
      val accounts = UtilBson.tableTo[OldAccount](document, "accounts", { a =>
        OldAccount.fromBSON(a)
      })
      val providers = UtilBson.tableTo[OldProviderUser](document, "distants", { d =>
        OldProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[OldColumn](document, "columns", { c =>
        OldColumn.fromBSON(c)
      })
      OldUser(accounts, Some(providers), Some(columns))
    }
  }
  
  def toUser(oldUser: OldUser): models.User = {
    models.User.create("")
  }
  
}