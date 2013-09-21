package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class Feature(
    name: String,
    isEnable: Boolean
)

object Feature {
  
  implicit val reader = (
    (__ \ "name").read[String] and
    (__ \ "isEnable").read[Boolean])(Feature.apply _)

  implicit val writer = (
    (__ \ "name").write[String] and
    (__ \ "isEnable").write[Boolean])(unlift(Feature.unapply))
    
  def toBSON(feature: Feature) = {
    BSONDocument(
      "name" -> BSONString(feature.name),
      "isEnable" -> BSONBoolean(feature.isEnable))
  }
  
  def fromBSON(c: BSONDocument) = {
    Feature(
        c.getAs[String]("name").get,
        c.getAs[Boolean]("isEnable").get)
  }
  
}