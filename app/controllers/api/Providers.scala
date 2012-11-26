package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import models.Service
import play.api.libs.json._
import services.security.AuthenticatedAction.Authenticated
import services.actors.ProviderActor
import models.command.Command
import play.api.http.ContentTypes._

object Providers extends Controller {

  def listAll = Action { implicit req =>
    Ok(Service.toJson())
  }
  
  def listServices = Action { implicit req =>
    Ok(Service.toJsonWithUnifiedRequest)
  }
  
  def delete(providerName: String) = Authenticated { action =>
    implicit val request = action.request

    ProviderDispatcher(providerName).map { provider =>
      action.user.accounts.foreach { account =>
        ProviderActor.killProfider(account.id, providerName)
      }
      provider.deleteToken
      val command = Command("deleteProvider", Some(JsString("ok")))
      Ok(Json.toJson(command))
    }.getOrElse(BadRequest)
  }

}