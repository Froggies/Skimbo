package models;

import play.api.libs.json._
import services.auth.ProviderDispatcher
import play.api.mvc.RequestHeader
import services.endpoints.Endpoints

case class Service(name: String, connected: Boolean)

object Service {

  def list(implicit request: RequestHeader) = {
    ProviderDispatcher.listAll.map(provider => Service(provider.name, provider.hasToken))
  }
  
  def toJson(implicit request: RequestHeader) = {
    val services = list.map { service =>
      Json.obj(
        "name" -> service.name,
        "connected" -> service.connected
      )
    }
    Json.toJson(services)
  }
  
  def toJsonWithUnifiedRequest(implicit request: RequestHeader) = {
    val jsonUnifiedRequests = Endpoints.getAll.map { endpoint =>
      Json.obj(
        "endpoint" -> endpoint.provider.name,
        "services" -> endpoint.services.map(service => Json.obj(
          "service" -> service.uniqueName,
          "args" -> service.requiredParams,
          "hasParser" -> service.parser.isDefined,
          "hasHelper" -> service.paramParserHelper.isDefined
        )),
        "hasToken" -> endpoint.provider.canStart
      )
    }
    Json.toJson(jsonUnifiedRequests)
  }
  
}
