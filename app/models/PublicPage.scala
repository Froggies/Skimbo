package models

import models.user.Column
import models.user.ProviderUser
import reactivemongo.bson.handlers.BSONWriter
import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONString
import reactivemongo.bson.handlers.BSONReader

case class PublicPage(
  name: String,
  emailOwner: String,
  distantsOwner: Option[Seq[ProviderUser]] = None,
  columns: Option[Seq[Column]] = None)

object PublicPage {
  
  implicit object PublicPageBSONReader extends BSONReader[PublicPage] {
    def fromBSON(document: BSONDocument): PublicPage = {
      val traversable = document.toTraversable
      val providers = UtilBson.tableTo[ProviderUser](document, "distants", { d =>
        ProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[Column](document, "columns", { c =>
        Column.fromBSON(c)
      })
      PublicPage(
        UtilBson.asString(traversable, "name"), 
        UtilBson.asString(traversable, "email"), 
        Some(providers), 
        Some(columns))
    }
  }
  
  implicit object PublicPageBSONWriter extends BSONWriter[PublicPage] {
    def toBSON(publicPage: PublicPage): BSONDocument = {
      val distants = UtilBson.toArray[ProviderUser](publicPage.distantsOwner.getOrElse(Seq()), { distant =>
        ProviderUser.toBSON(distant)
      })

      val columns = UtilBson.toArray[Column](publicPage.columns.getOrElse(Seq()), { column =>
        Column.toBSON(column)
      })

      BSONDocument(
        "name" -> BSONString(publicPage.name),
        "email" -> BSONString(publicPage.emailOwner),
        "distants" -> distants,
        "columns" -> columns
      )
    }
  }
  
}