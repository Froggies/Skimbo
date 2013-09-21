package oldModel

import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONDocumentReader
import models.user.OptionUser

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
    
    val accounts = oldUser.accounts.map { account =>
      models.user.Account(account.id, account.lastUse, "", "Skimbo", Seq.empty)
    }
    
    val distants = oldUser.distants.getOrElse(Seq.empty).map { distant =>
      val token = distant.token.map { token =>
        models.user.SkimboToken(
          token.token,
          token.secret
        )
      }
      models.user.ProviderUser(
          distant.id,
          distant.socialType,
          token
      )
    }
    
    val columns = oldUser.columns.getOrElse(Seq.empty).map { column =>
      val ur = column.unifiedRequests.map( unifiedRequest =>
          models.user.UnifiedRequest(unifiedRequest.service, unifiedRequest.args)
      )
      models.user.Column(
          column.title,
          ur,
          column.index,
          column.width,
          column.height
      )
    }
    
    models.User(
        OptionUser.create,
        accounts,
        distants,
        columns,
        Seq.empty
    )
  }
  
}