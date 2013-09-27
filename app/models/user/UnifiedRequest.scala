package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson
import parser.json.PathDefaultReads

case class UnifiedRequest(
    service: String,
    args: Seq[ServiceArg],
    uidProviderUser: String,
    sinceId: Option[String]
)

object UnifiedRequest {

  implicit val unifiedRequestReader: Reads[UnifiedRequest] = new Reads[UnifiedRequest] {
    def reads(js: JsValue): JsSuccess[UnifiedRequest] = {
      JsSuccess(UnifiedRequest(
        (js \ "service").as[String],
        (js \ "args").as[Seq[ServiceArg]],
        (js \ "uidProviderUser").as[String],
        None
      ))
    }
  }

  implicit val writes = new Writes[UnifiedRequest] {
    def writes(unifiedRequest: UnifiedRequest): JsValue = {
      Json.obj(
        "service" -> unifiedRequest.service,
        "args" -> unifiedRequest.args,
        "uidProviderUser" -> unifiedRequest.uidProviderUser,
        "sinceId" -> unifiedRequest.sinceId
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
      "uidProviderUser" -> BSONString(unifiedRequest.uidProviderUser),
      "sinceId" -> unifiedRequest.sinceId
    )
  }

  def fromBSON(c: BSONDocument) = {
    val args = UtilBson.tableTo[ServiceArg](c, "args", { r =>
      ServiceArg.fromBSON(r)
    })
    UnifiedRequest(
        c.getAs[String]("service").get, 
        args,
        c.getAs[String]("uidProviderUser").get,
        c.getAs[String]("sinceId")
    )
  }
  
}