package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import models.Service
import play.api.libs.json._
import services.security.AuthenticatedAction.Authenticated
import services.actors.ProviderActor
import models.command.Command

object Providers extends Controller {

  def listAll = Action { implicit req =>
    Ok(Service.toJson()).as(play.api.http.ContentTypes.JSON)
  }
  
  def listServices = Action {  implicit req =>
    Ok(Service.toJsonWithUnifiedRequest).as(play.api.http.ContentTypes.JSON)
  }
  
  def delete(providerName: String) = Authenticated { action =>
    val providerOpt  = ProviderDispatcher(providerName);
    providerOpt.map { provider =>
      action.user.accounts.foreach { account =>
        ProviderActor.killProfider(account.id, providerName)
      }
      Ok(Json.toJson(Command("deleteProvider", Some(JsString("ok"))))).as(play.api.http.ContentTypes.JSON);
    }.getOrElse(BadRequest)
  }

}