package controllers

import play.api.libs.EventSource
import play.api.mvc._
import services.actors.ProviderActor
import services.auth.ProviderDispatcher
import services.auth.providers._
import models.Service
import play.api.libs.json.JsValue
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import scala.concurrent.util.duration._
import scala.concurrent.Await
import java.io.File
import services.actors.UserInfosActor
import services.security.AuthenticatedAction._
import play.api.Logger

object Application extends Controller {

  def index = Action { implicit request =>
    request.session.get("id").map(userId => {
      //UserInfosActor(userId)//RM : decomment this if you want to test bd retreive
      Ok(views.html.unified())
    }).getOrElse(Ok(views.html.index(Service.list)))
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)).getOrElse(BadRequest)
  }
  
  def logout() = Authenticated { action =>
    Logger.info("Aplication.scala :: KillMyActors :: " + action.user.id)
    ProviderActor.killActorsForUser(action.user.id)
    //TODO remove session id and redirect to index.html
    Ok("ok")
  }

}