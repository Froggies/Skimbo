package models;

import play.api.libs.json._
import services.auth.ProviderDispatcher
import play.api.mvc.RequestHeader

case class Service(name: String, logo: String, connected: Boolean)

object Service {
  
  def list(implicit req: RequestHeader) = {
    ProviderDispatcher.listAll.map(provider =>
      Service(provider.name, provider.logo, provider.hasToken))
  }
}
