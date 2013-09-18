package models

import models.user.Column
import models.user.ProviderUser
import reactivemongo.bson.BSONDocument
import services.dao.UtilBson
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson._

case class StatsToken(
    providerName: String,
    nbToken: Int
)

case class StatsX(
    nbUser: Int,
    nbX: Int
)

case class StatsApp(
    timestamp: Long,
    nbUsers: Int,
    nbConnexion: Int,
    nbToken: Seq[StatsToken],
    nbAccounts: Seq[StatsX],
    nbColumns: Seq[StatsX],
    nbServices: Seq[StatsX],
    nbServicesByColumn: Seq[StatsX],
    nbUserActif: StatsX,
    nbChannelActif: StatsX,
    nbProviderActif: StatsX,
    multipleChannel: Int
)

object StatsApp {
  
  def toBSON(statX: StatsX): BSONDocument = BSONDocument("nbUser" -> statX.nbUser, "nbX" -> statX.nbX)
  def toBSON(statsX: Seq[StatsX]): BSONArray = UtilBson.toArray[StatsX](statsX, { statX => toBSON(statX) })
  
  def fromBSON(a: BSONDocument): StatsX = StatsX(a.getAs[BSONInteger]("nbUser").get.value, a.getAs[BSONInteger]("nbX").get.value)
  def fromBSON(document: BSONDocument, p: String): Seq[StatsX] = UtilBson.tableTo[StatsX](document, p, { a => fromBSON(a) })
  
  implicit object StatsAppBSONReader extends BSONDocumentReader[StatsApp] {
    def read(document: BSONDocument): StatsApp = {
      val nbTokens = UtilBson.tableTo[StatsToken](document, "nbToken", { a =>
        StatsToken(
            a.getAs[BSONString]("providerName").get.value,
            a.getAs[BSONInteger]("nbToken").get.value
        )
      })
      
      val nbAccounts = fromBSON(document, "nbAccounts")
      val nbColumns = fromBSON(document, "nbColumns")
      val nbServices = fromBSON(document, "nbServices")
      val nbServicesByColumn = fromBSON(document, "nbServicesByColumn")
      
      StatsApp(
          document.getAs[BSONLong]("timestamp").get.value,
          document.getAs[BSONInteger]("nbUsers").get.value,
          document.getAs[BSONInteger]("nbConnexion").get.value,
          nbTokens,
          nbAccounts,
          nbColumns,
          nbServices,
          nbServicesByColumn,
          fromBSON(document.getAs[BSONDocument]("nbUserActif").get),
          fromBSON(document.getAs[BSONDocument]("nbChannelActif").get),
          fromBSON(document.getAs[BSONDocument]("nbProviderActif").get),
          document.getAs[BSONInteger]("multipleChannel").get.value
      )
    }
  }
  
  implicit object StatsAppBSONWriter extends BSONDocumentWriter[StatsApp] {
    def write(statsApp: StatsApp): BSONDocument = {
      val nbTokens = UtilBson.toArray[StatsToken](statsApp.nbToken, { statsToken =>
        BSONDocument("providerName" -> statsToken.providerName, "nbToken" -> statsToken.nbToken)
      })
      
      val nbAccounts = toBSON(statsApp.nbAccounts)
      val nbColumns = toBSON(statsApp.nbColumns)
      val nbServices = toBSON(statsApp.nbServices)
      val nbServicesByColumn = toBSON(statsApp.nbServicesByColumn)
      
      BSONDocument(
        "timestamp" -> statsApp.timestamp,
        "nbUsers" -> statsApp.nbUsers,
        "nbConnexion" -> statsApp.nbConnexion,
        "nbToken" -> nbTokens,
        "nbAccounts" -> nbAccounts,
        "nbColumns" -> nbColumns,
        "nbServices" -> nbServices,
        "nbServicesByColumn" -> nbServicesByColumn,
        "nbUserActif" -> toBSON(statsApp.nbUserActif),
        "nbChannelActif" -> toBSON(statsApp.nbChannelActif),
        "nbProviderActif" -> toBSON(statsApp.nbProviderActif),
        "multipleChannel" -> statsApp.multipleChannel
      )
    }
  }
  
}