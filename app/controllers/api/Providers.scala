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
    val idUser = req.session.get("id").getOrElse("")
    Ok(Service.toJson(idUser))
  }
  
  def listServices = Action { implicit req =>
    val idUser = req.session.get("id").getOrElse("")
    Ok(Service.toJsonWithUnifiedRequest(idUser))
  }
  
  def delete(providerName: String) = Authenticated { user => implicit request =>
      val idUser = user.accounts.headOption.map(_.id)
      ProviderDispatcher(providerName).map { provider =>
        user.accounts.foreach { account =>
          ProviderActor.killProvider(account.id, providerName)
          provider.deleteToken(account.id)
        }
        Ok(Json.toJson(Command("deleteProvider", Some(JsString("ok")))))
      }.getOrElse(BadRequest)
  }

}