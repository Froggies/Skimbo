package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class StatUser(
    timestamp: Long,
    provider: String,
    service: String,
    action: String,
    idProvider: String,
    value: Int
)

object StatUser {
  
  implicit val reader = (
    (__ \ "timestamp").read[Long] and
    (__ \ "provider").read[String] and
    (__ \ "service").read[String] and
    (__ \ "action").read[String] and
    (__ \ "idProvider").read[String] and
    (__ \ "value").read[Int])(StatUser.apply _)

  implicit val writer = (
    (__ \ "timestamp").write[Long] and
    (__ \ "provider").write[String] and
    (__ \ "service").write[String] and
    (__ \ "action").write[String] and
    (__ \ "idProvider").write[String] and
    (__ \ "value").write[Int])(unlift(StatUser.unapply))
    
  def toBSON(statUser: StatUser) = {
    BSONDocument(
      "timestamp" -> BSONLong(statUser.timestamp),
      "provider" -> BSONString(statUser.provider),
      "service" -> BSONString(statUser.service),
      "action" -> BSONString(statUser.action),
      "idProvider" -> BSONString(statUser.idProvider),
      "value" -> BSONInteger(statUser.value))
  }
  
  def fromBSON(c: BSONDocument) = {
    StatUser(
        c.getAs[Long]("timestamp").get,
        c.getAs[String]("provider").get,
        c.getAs[String]("service").get,
        c.getAs[String]("action").get,
        c.getAs[String]("idProvider").get,
        c.getAs[Int]("value").get)
  }
  
}