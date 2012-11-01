package services.actors;

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.PlayException
import play.api.UnexpectedException

sealed case class Dead(idUser: String)

object ProviderActor {

  val system: ActorSystem = ActorSystem("providers");

  def create(endpoints: Seq[Endpoint])(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    endpoints.map{ endpoint =>
      val actor = system.actorOf(Props(new ProviderActor(channel, endpoint)))
      system.eventStream.subscribe(actor, classOf[Dead])
    }
    rawStream
  }

  def killActorsForUser(userId: String) = {
    system.eventStream.publish(Dead(userId))
  }

}

class ProviderActor(channel: Concurrent.Channel[JsValue], endpoint: Endpoint)(implicit request: RequestHeader) extends Actor {

  val scheduler = Akka.system.scheduler.schedule(0 second, endpoint.interval second) {
    self ! ReceiveTimeout
  }

  def receive = {
    case ReceiveTimeout => {
      println("actor provider pull " + endpoint.provider.name + " on " + endpoint.url)
      endpoint.provider.fetch(endpoint.url).get.await(10000).fold( // TODO : Virer cet await que je ne saurais voir !
        error => {
          channel.push(Json.toJson("error with " + endpoint.provider.name))
          self ! Dead
        },
        response => 
          channel.push(response.json))
    }
    
    case Dead(idUser) => {
      if (idUser == endpoint.idUser) {
        println("actor provider kill for " + idUser)
        scheduler.cancel()
        context.stop(self)
      }
    }
    
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

}