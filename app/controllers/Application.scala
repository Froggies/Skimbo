package controllers

import model._

import play.api._
import play.api.mvc._
import play.api.libs._
import play.api.libs.json._
import play.api.libs.json.Json._
import services.auth.providers._
import play.api.libs.iteratee._
import play.api.libs.ws.{Response => ResponseWS, _}

import play.api.libs.concurrent._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Social Feeds"))
  }

  def authenticate(provider: String) = Action { implicit request =>
    provider match {
      case GitHub.name        => GitHub.auth(routes.SocialNetworksTest.github)
      case Facebook.name      => Facebook.auth(routes.SocialNetworksTest.facebook)
      case Twitter.name       => Twitter.auth(routes.SocialNetworksTest.twitter)
      case GooglePlus.name    => GooglePlus.auth(routes.SocialNetworksTest.googleplus)
      case LinkedIn.name      => LinkedIn.auth(routes.SocialNetworksTest.linkedin)
      case StackExchange.name => StackExchange.auth(routes.SocialNetworksTest.stackexchange)
      case Trello.name        => Trello.auth(routes.SocialNetworksTest.trello)
      case Twitter.name       => Twitter.auth(routes.SocialNetworksTest.twitter)
      case Viadeo.name        => Viadeo.auth(routes.SocialNetworksTest.viadeo)
      case Scoopit.name       => Scoopit.auth(routes.SocialNetworksTest.scoopit)
      case _                  => throw new PlayException("Provider not implemented", "The provider "+provider+" is not implemented")
    }
  }

  def testActor() = Action { implicit request =>
    Trello.getToken match {
      case Some(credentials) => {
        val sse = SseActor.operations(request, Trello, "https://api.trello.com/1/members/me/notifications");
        Ok.feed(sse &> EventSource()).as("text/event-stream")
      }
    }
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