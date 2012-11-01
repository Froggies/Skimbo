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

object Application extends Controller {

  def index = Action { implicit request =>
    request.session.get("id").map(_ => {
      val userId = request.session.get("id").getOrElse("None")
      //UserInfosActor(Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      //UserInfosActor(Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      //UserInfosActor(Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      //UserInfosActor(Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      //UserInfosActor(Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      //DONT WORK UserInfosActor(Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      UserInfosActor(Endpoint(GitHub, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
      Ok(views.html.unified())
    }).getOrElse(Ok(views.html.index(Service.list)))
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)).getOrElse(BadRequest)
  }

  def testActor2() = Action { implicit request =>
    val userId = request.session.get("id").getOrElse("None") //FIXME : meilleurs moyen pour récupérer l'unique id
    val endpoints = Seq()
      //Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 3, userId))
    //Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 5),
    //Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 3))

    val enumerator = ProviderActor.create(endpoints)
    // -> to Skimbo 
    // -> trier par date
    // -> filter en fonction des déjà vus
    // -> to Json

    //Ok.feed(enumerator &> EventSource()).as(EVENT_STREAM) // what'is the difference ?
    Ok.stream(enumerator &> EventSource()).as(EVENT_STREAM)
  }

  def killMyActors() = Action { implicit request =>
    val userId = request.session.get("id").getOrElse("None")
    println("Aplication.scala :: KillMyActors :: " + userId)
    ProviderActor.killActorsForUser(userId)
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