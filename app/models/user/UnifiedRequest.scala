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
    sinceId: Seq[SinceId]
)

object UnifiedRequest {
  
  def equals(unifiedRequest:UnifiedRequest, unifiedRequest2:UnifiedRequest, checkSinceId:Boolean=false):Boolean = {
    unifiedRequest.service == unifiedRequest2.service &&
    unifiedRequest.uidProviderUser == unifiedRequest2.uidProviderUser &&
    ((unifiedRequest.args.isEmpty && unifiedRequest2.args.isEmpty) ||
        unifiedRequest.args.exists(a1 => unifiedRequest2.args.exists(a2 => a1.value.call == a2.value.call))) &&
    (!checkSinceId || 
        unifiedRequest.sinceId.exists(s1 => unifiedRequest2.sinceId.exists(s2 => s1.accountId == s2.accountId && s1.sinceId == s2.sinceId))
    )
  }

  def merge(unifiedRequest:UnifiedRequest, sinceId: SinceId): UnifiedRequest =  {
    val optSinceId = unifiedRequest.sinceId.filter(_.accountId == sinceId.accountId).headOption
    optSinceId.map { s => 
      UnifiedRequest(
          unifiedRequest.service,
          unifiedRequest.args,
          unifiedRequest.uidProviderUser,
          unifiedRequest.sinceId.filterNot(_.accountId == sinceId.accountId) ++ Seq(sinceId)
      )
    }.getOrElse {
      UnifiedRequest(
          unifiedRequest.service,
          unifiedRequest.args,
          unifiedRequest.uidProviderUser,
          unifiedRequest.sinceId ++ Seq(sinceId)
      )
    }
  }
  
  implicit val unifiedRequestReader: Reads[UnifiedRequest] = new Reads[UnifiedRequest] {
    def reads(js: JsValue): JsSuccess[UnifiedRequest] = {
      JsSuccess(UnifiedRequest(
        (js \ "service").as[String],
        (js \ "args").as[Seq[ServiceArg]],
        (js \ "uidProviderUser").as[String],
        Seq.empty//client no send sinceId
      ))
    }
  }

  implicit val writes = new Writes[UnifiedRequest] {
    def writes(unifiedRequest: UnifiedRequest): JsValue = {
      Json.obj(
        "service" -> unifiedRequest.service,
        "args" -> unifiedRequest.args,
        "uidProviderUser" -> unifiedRequest.uidProviderUser,
        "sinceId" -> Seq[SinceId]()//client no need sinceId
      )
    }
  }
  
  def toBSON(unifiedRequest: UnifiedRequest) = {
    val args = UtilBson.toArray[ServiceArg](unifiedRequest.args, { arg =>
      ServiceArg.toBSON(arg)
    })
    val sinceId = UtilBson.toArray[SinceId](unifiedRequest.sinceId, { sinceId =>
      SinceId.toBSON(sinceId)
    })
    BSONDocument(
      "service" -> BSONString(unifiedRequest.service),
      "args" -> args,
      "uidProviderUser" -> BSONString(unifiedRequest.uidProviderUser),
      "sinceId" -> sinceId
    )
  }

  def fromBSON(c: BSONDocument) = {
    val args = UtilBson.tableTo[ServiceArg](c, "args", { r =>
      ServiceArg.fromBSON(r)
    })
    val sinceId = UtilBson.tableTo[SinceId](c, "sinceId", { r =>
      SinceId.fromBSON(r)
    })
    UnifiedRequest(
        c.getAs[String]("service").get, 
        args,
        c.getAs[String]("uidProviderUser").get,
        sinceId
    )
  }
  
}