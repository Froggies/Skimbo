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
    val json = list.map { service =>
      Json.obj(
        "name" -> JsString(service.name),
        "connected" -> JsBoolean(service.connected)
      )
    }
    JsArray(json)
  }
  
  def toJsonWithUnifiedRequest()(implicit req: RequestHeader):JsValue = {
    val jsonUnifiedRequests = Endpoints.getAll.map { endpoint =>
      Json.obj(
        "endpoint" -> endpoint.provider.name,
        "services" -> JsArray(
          endpoint.services.map { service =>
            Json.obj(
              "name" -> service.service,
              "args" -> service.configuration.requiredParams
            )
          }
        ),
        "hasToken" -> endpoint.provider.hasToken
      )
    }
    JsArray(jsonUnifiedRequests.toList)
  }
  
}
