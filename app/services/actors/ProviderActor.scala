package services.actors;

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{ Concurrent, Enumerator }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.PlayException
import play.api.UnexpectedException
import play.api.libs.concurrent.execution.defaultContext

sealed case class Ping(idUser: String)
sealed case class Dead(idUser: String = "")

object ProviderActor {

  val system: ActorSystem = ActorSystem("providers");

  def create(endpoints: Seq[Endpoint])(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    endpoints.map { endpoint =>
      val actor = system.actorOf(Props(new ProviderActor(channel, endpoint)))
      system.eventStream.subscribe(actor, classOf[Dead])
      system.eventStream.subscribe(actor, classOf[Ping])
    }
    rawStream
  }

  def ping(userId: String) = {
    system.eventStream.publish(Ping(userId))
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
      if (endpoint.longPolling) {
        scheduler.cancel() //need ping to call provider
      }
      if (endpoint.provider.hasToken(request)) { //TODO RM : remove when api endpoint from JL was done
        endpoint.provider.fetch(endpoint.url).get.map(response => channel.push(response.json))
      } else {
        self ! Dead(endpoint.idUser)
      }
    }
    case Ping(idUser) => {
      if (idUser == endpoint.idUser) {
        Akka.system.scheduler.scheduleOnce(endpoint.interval second) {
          self ! ReceiveTimeout
        }
      }
    }
    case Dead(idUser) => {
      if (idUser == endpoint.idUser) {
        scheduler.cancel()
        context.stop(self)
      }
    }

    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

}