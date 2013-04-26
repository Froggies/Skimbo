package models

import models.user.Column
import models.user.ProviderUser
import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter

case class PublicPage(
  name: String,
  emailOwner: String,
  distantsOwner: Option[Seq[ProviderUser]] = None,
  columns: Option[Seq[Column]] = None)

object PublicPage {
  
  implicit object PublicPageBSONReader extends BSONDocumentReader[PublicPage] {
    def read(document: BSONDocument): PublicPage = {
      val providers = UtilBson.tableTo[ProviderUser](document, "distants", { d =>
        ProviderUser.fromBSON(d)
      })
      val columns = UtilBson.tableTo[Column](document, "columns", { c =>
        Column.fromBSON(c)
      })
      PublicPage(
        document.getAs[String]("name").get, 
        document.getAs[String]("email").get, 
        Some(providers), 
        Some(columns))
    }
  }
  
  implicit object PublicPageBSONWriter extends BSONDocumentWriter[PublicPage] {
    def write(publicPage: PublicPage): BSONDocument = {
      val distants = UtilBson.toArray[ProviderUser](publicPage.distantsOwner.getOrElse(Seq()), { distant =>
        ProviderUser.toBSON(distant)
      })

      val columns = UtilBson.toArray[Column](publicPage.columns.getOrElse(Seq()), { column =>
        Column.toBSON(column)
      })

      BSONDocument(
        "name" -> publicPage.name,
        "email" -> publicPage.emailOwner,
        "distants" -> distants,
        "columns" -> columns
      )
    }
  }
  
}