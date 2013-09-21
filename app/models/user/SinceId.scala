package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class SinceId (
    uidProviderUser: String,
    sinceId: String
)

object SinceId {
  
  implicit val writer = (
    (__ \ "uidProviderUser").write[String] and
    (__ \ "sinceId").write[String]
  )(unlift(SinceId.unapply))
  
  def toBSON(sinceId: SinceId) = {
    BSONDocument( 
      "uidProviderUser" -> BSONString(sinceId.uidProviderUser),
      "sinceId" -> BSONString(sinceId.sinceId))
  }
  
  def fromBSON(a: BSONDocument) = {
    SinceId(
        a.getAs[String]("uidProviderUser").get, 
        a.getAs[String]("sinceId").get)
  }
  
}