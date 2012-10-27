package services.actors;

import akka.actor._
import akka.util.duration._
import play.api.libs.concurrent.futureToPlayPromise // TODO JLA : Regarder comment rester sur les Future
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc.RequestHeader
import play.libs.Akka
import services.auth.GenericProvider

object ProviderActor {

  def apply(endpoints: Seq[Endpoint])(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    endpoints.foreach({endpoint => 
      val actor = Akka.system.actorOf(Props(new ProviderActor(channel, endpoint)))
      rawStream.onDoneEnumerating({
        println("ACTOR DONE "+endpoint.provider.name)
        actor ! Kill
      })
    })
    rawStream
  }
}

class ProviderActor(channel: Concurrent.Channel[JsValue], endpoint: Endpoint)(implicit request: RequestHeader) extends Actor {

  context.setReceiveTimeout(endpoint.interval seconds)

  def receive = {
    case ReceiveTimeout => {
      println("ACTOR GO "+endpoint.provider.name)
      endpoint.provider.fetch(endpoint.url).get.await(10000).fold(
        error => {
          channel.push(Json.toJson("error with "+endpoint.provider.name))
          self ! Kill
        },
        response => channel.push(response.json))
    }
    case Kill => {
      println("ACTOR KILL "+endpoint.provider.name)
      context.stop(self)
    }
    case e: Exception => println("unexpected Error: "+e)
  }

}