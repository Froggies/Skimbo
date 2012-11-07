package controllers

import play.api.libs.EventSource
import play.api.mvc._
import services.actors.ProviderActor
import services.auth.ProviderDispatcher
import services.auth.providers._
import services.actors.Endpoint
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
      //TODO RM : remove when api endpoint from JL was done
      val endpoints = Seq(
        Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 5, userId, true),
//        Endpoint(GitHub, "http://dev.studio-dev.fr/test-ws-json.php?nom=github", 10, userId, true),
//        Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 15, userId, true),
//        Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=googlePlus", 3, userId, true),
//        Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=linkedIn", 8, userId, true),
        Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=scoopit", 5, userId, true))
//        Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=stackExchange", 6, userId, true),
//        Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=trello", 5, userId, true),
//        Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 15, userId, true))
      //UserInfosActor(userId, endpoints)//RM : decomment this if you want to test bd retreive
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