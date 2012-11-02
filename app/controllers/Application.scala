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
      //RM : keep this for bd test when reactivemongo run
    //UserInfosActor(Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //UserInfosActor(Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //UserInfosActor(Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //UserInfosActor(Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //UserInfosActor(Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //DONT WORK UserInfosActor(Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //UserInfosActor(Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      Ok(views.html.unified())
    }).getOrElse(Ok(views.html.index(Service.list)))
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)).getOrElse(BadRequest)
  }

  def testActor2() = Authenticated { action =>
    implicit val request = action.request
    val user = action.user // (Pour la suite)

    //TODO RM : remove when api endpoint from JL was done
    val endpoints = Seq(
      Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 5, action.user.id),
      Endpoint(GitHub, "http://dev.studio-dev.fr/test-ws-json.php?nom=github", 10, action.user.id),
      Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 15, action.user.id),
      Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=googlePlus", 3, action.user.id),
      Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=linkedIn", 8, action.user.id),
      Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=scoopit", 5, action.user.id),
      Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=stackExchange", 6, action.user.id),
      Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=trello", 5, action.user.id),
      Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 15, action.user.id))

    val enumerator = ProviderActor.create(endpoints)
    // -> to Skimbo 
    // -> trier par date
    // -> filter en fonction des déjà vus
    // -> to Json
    Ok.stream(enumerator &> EventSource()).as(EVENT_STREAM)
  }

  def killMyActors() = Authenticated { action =>
    Logger.info("Aplication.scala :: KillMyActors :: " + action.user.id)
    ProviderActor.killActorsForUser(action.user.id)
    Ok("ok")
  }

  def connected() = WebSocket.using[String] { request =>
    // Log events to the console
    val in = Iteratee.foreach[String](println).mapDone { _ =>
      println("Disconnected")
    }

    // Send a single 'Hello!' message
    val out = Enumerator("Hello!")

    (in, out)
  }

}