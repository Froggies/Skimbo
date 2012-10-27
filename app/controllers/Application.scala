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
import play.api.libs.concurrent.execution.defaultContext
import play.api.libs.concurrent.Promise
import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import play.api.libs.ws.WS
import play.api.Play.current

import akka.util.Timeout
import akka.util.duration._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.unified())
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)
    ).getOrElse(BadRequest)
  }

  def testActor2() = Action { implicit request =>
	  lazy val sse = UserActor.create(Twitter, "https://api.twitter.com/1.1/statuses/home_timeline.json");
	  Ok.feed(sse &> EventSource()).as("text/event-stream")
  }
  
  def testActor() = Action { implicit request =>
    Twitter.getToken.map{ token =>
      println("IN")
      val sse = ProviderEnumerator(Twitter, "https://api.twitter.com/1.1/statuses/home_timeline.json");
      Ok.feed(sse &> EventSource()).as("text/event-stream")
    }.getOrElse(BadRequest)
  }

}

object ProviderEnumerator extends Results {

  def apply(provider: GenericProvider, endpoint: String)(implicit req: RequestHeader) : Enumerator[JsValue] = { 
    Enumerator.generateM[JsValue] {
      Promise.timeout( { 
        provider.fetch(endpoint).get.await(10000).fold( 
            onError => None,
            response => Some(response.json))
      }, 5, TimeUnit.SECONDS)
    }
  }
  
    
}