package models.user

import models.ParamHelper
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class ServiceArg (
    name: String,
    value: ParamHelper
)

object ServiceArg {

  implicit val serviceArgReader: Reads[ServiceArg] = (
    (__ \ "name").read[String] and
    (__ \ "value").read[ParamHelper])(ServiceArg.apply _)
        
  implicit val writer = (
    (__ \ "name").write[String] and
    (__ \ "value").write[ParamHelper]
  )(unlift(ServiceArg.unapply))
  
  def toBSON(serviceArg: ServiceArg) = {
    BSONDocument(
      "name" -> BSONString(serviceArg.name),
      "value" -> ParamHelper.toBSON(serviceArg.value))
  }

  def fromBSON(c: BSONDocument) = {
    ServiceArg(c.getAs[String]("name").get, ParamHelper.fromBSON(c.getAs[BSONDocument]("value").get))
  }
  
}