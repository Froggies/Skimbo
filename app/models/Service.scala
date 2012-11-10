package models;

import play.api.libs.json._
import services.auth.ProviderDispatcher
import play.api.mvc.RequestHeader
import services.endpoints.Endpoints

case class Service(name: String, connected: Boolean)

object Service {

  def list(implicit req: RequestHeader) = {
    ProviderDispatcher.listAll.map(provider =>
      Service(provider.name, provider.hasToken))
  }
  
  def toJson()(implicit req: RequestHeader):JsValue = {
    val jsonUnifiedRequests = Endpoints.endpoints.map { endpoint =>
      JsObject(Seq(
        "endpoint" -> JsString(endpoint._2.provider.name),
        "hasToken" -> JsBoolean(endpoint._2.provider.hasToken),
        "service" -> JsString(endpoint._1),
        "args" -> JsArray(endpoint._2.requiredParams.map(JsString(_)))
      ))
    }
    JsArray(jsonUnifiedRequests.toList)
  }
  
}
