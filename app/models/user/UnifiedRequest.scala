package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class UnifiedRequest(
    service: String,
    args: Seq[ServiceArg],
    uidProviderUser: String
)

object UnifiedRequest {

  implicit val unifiedRequestReader: Reads[UnifiedRequest] = (
    (__ \ "service").read[String] and
    (__ \ "args").read[Seq[ServiceArg]] and
    (__ \ "uidProviderUser").read[String])(UnifiedRequest.apply _)
        
  implicit val writes = new Writes[UnifiedRequest] {
    def writes(unifiedRequest: UnifiedRequest): JsValue = {
      Json.obj(
        "service" -> unifiedRequest.service,
        "args" -> unifiedRequest.args,
        "uidProviderUser" -> unifiedRequest.uidProviderUser
      )
    }
  }
  
  def toBSON(unifiedRequest: UnifiedRequest) = {
    val args = UtilBson.toArray[ServiceArg](unifiedRequest.args, { arg =>
      ServiceArg.toBSON(arg)
    })
    BSONDocument(
      "service" -> BSONString(unifiedRequest.service),
      "args" -> args,
      "uidProviderUser" -> BSONString(unifiedRequest.uidProviderUser)
    )
  }

  def fromBSON(c: BSONDocument) = {
    val args = UtilBson.tableTo[ServiceArg](c, "args", { r =>
      ServiceArg.fromBSON(r)
    })
    UnifiedRequest(
        c.getAs[String]("service").get, 
        args,
        c.getAs[String]("uidProviderUser").get)
  }
  
}