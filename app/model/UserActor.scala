package model;

import akka.actor._
import akka.util.duration._
import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import akka.util.Timeout
import akka.pattern.ask
import play.api.Play.current
import play.api.libs.concurrent.execution.defaultContext
import services.auth.GenericProvider
import play.api.mvc.RequestHeader
import akka.util.Duration
import services.auth.GenericProvider
import java.util.concurrent.TimeUnit
import play.api.libs.iteratee.PushEnumerator
import play.libs.Scala
import scala.concurrent.Future
import play.libs.Akka
import play.api.libs.iteratee.Concurrent
import akka.actor.ReceiveTimeout

object UserActor {

  implicit val timeout = Timeout(100 second)

  def create(provider: GenericProvider, endpoint: String)(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    val actor = Akka.system.actorOf(Props(new UserActor(channel, provider, endpoint)))
    rawStream
  }
}

class UserActor(channel:Concurrent.Channel[JsValue], provider: GenericProvider, endpoint: String)(implicit request: RequestHeader) extends Actor {

  val interval = 5; //Config.getLong("mail.check.interval")
  context.setReceiveTimeout(akka.util.Duration(interval, TimeUnit.SECONDS))
  
  def receive = {
    case ReceiveTimeout => {
      dispatch()
    }
    case e: Exception => println("unexpected Error: " + e)
  }

  def dispatch() {
    println("dispatch actor"+context.sender)
    channel.push(provider.fetch(endpoint).get.await(10000).fold(
                        onError => Json.toJson("error"),
                        response => response.json)); 
  }

}
