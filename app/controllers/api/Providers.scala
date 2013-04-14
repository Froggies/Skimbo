package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import models.Service
import play.api.libs.json._
import services.actors.ProviderActor
import models.command.Command
import play.api.http.ContentTypes._
import services.security.Authentication

object Providers extends Controller with Authentication {

  def listAll = Action { implicit req =>
    Ok(Service.toJson)
  }
  
  def listServices = Action { implicit req =>
    Ok(Service.toJsonWithUnifiedRequest)
  }
  
  def delete(providerName: String) = Authenticated { user =>
    implicit request =>
      
      ProviderDispatcher(providerName).map { provider =>
        user.accounts.foreach { account =>
          ProviderActor.killProvider(account.id, providerName)
        }
        provider.deleteToken
        Ok(Json.toJson(Command("deleteProvider", Some(JsString("ok")))))
      }.getOrElse(BadRequest)
  }

}