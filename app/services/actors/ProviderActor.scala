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

  // TODO : Remplacer le tuple3() par une CaseClass
  def apply(endpoints: Seq[(GenericProvider, String, Int)])(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    endpoints.foreach(endpoint => Akka.system.actorOf(Props(new ProviderActor(channel, endpoint))))
    rawStream
  }
}

class ProviderActor(channel: Concurrent.Channel[JsValue], endpoint: (GenericProvider, String, Int))(implicit request: RequestHeader) extends Actor {

  context.setReceiveTimeout(endpoint._3 seconds)

  def receive = {
    case ReceiveTimeout => {
      channel.push(endpoint._1.fetch(endpoint._2).get.await(10000).fold(
        error => Json.toJson("error"),
        response => response.json))
    }
    case e: Exception => println("unexpected Error: "+e)
  }

}