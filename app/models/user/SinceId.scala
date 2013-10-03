package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class SinceId (
    sinceId: String,
    accountId: String
)

object SinceId {
  
  implicit val reader = (
    (__ \ "sinceId").read[String] and
    (__ \ "accountId").read[String])(SinceId.apply _)

  implicit val writer = (
    (__ \ "sinceId").write[String] and
    (__ \ "accountId").write[String])(unlift(SinceId.unapply))
    
  def toBSON(sinceId: SinceId) = {
    BSONDocument(
      "sinceId" -> BSONString(sinceId.sinceId),
      "accountId" -> BSONString(sinceId.accountId))
  }
  
  def fromBSON(c: BSONDocument) = {
    SinceId(
        c.getAs[String]("sinceId").get,
        c.getAs[String]("accountId").get)
  }
  
}