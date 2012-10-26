package controllers

import model._
import services.auth._
import services.auth.providers._
import play.api._
import play.api.mvc._
import play.api.libs._
import play.api.libs.json._
import play.api.libs.json.Json._
import services.auth.providers._
import play.api.libs.iteratee._
import play.api.libs.ws.{Response => ResponseWS, _}
import scala.util.control.Breaks._
import play.api.libs.concurrent._
import services.auth.ProviderDispatcher

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.unified())
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)
    ).getOrElse(BadRequest)
  }

  def testActor() = Action { implicit request =>
    /*Trello.getToken.map{token =>
      val sse = SseActor.operations(request, Trello, "https://api.trello.com/1/members/me/notifications");
      Ok.feed(sse &> EventSource()).as("text/event-stream")
    }*/
  	Ok("hello")
  }

}