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
import play.api.Logger
import services.endpoints.Endpoints
import services.endpoints.JsonRequest._

sealed case class Ping(idUser: String)
sealed case class Dead(idUser: String = "")

object ProviderActor {

  val system: ActorSystem = ActorSystem("providers");

  def create(userId:String, unifiedRequests: Seq[UnifiedRequest])(implicit request: RequestHeader): (Enumerator[JsValue], Concurrent.Channel[JsValue]) = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    create(channel, userId, unifiedRequests)
    (rawStream, channel)
  }
  
  def create(channel:Concurrent.Channel[JsValue], userId:String, unifiedRequests: Seq[UnifiedRequest])(implicit request: RequestHeader) {
    unifiedRequests.foreach { unifiedRequest:UnifiedRequest =>
      val provider = Endpoints.getProvider(unifiedRequest.service)
      val url = Endpoints.genererUrl(unifiedRequest.service, unifiedRequest.args.getOrElse(Map.empty), None)
      if(provider.isDefined && url.isDefined) {
        Logger.info("Provider : "+provider.get.name)
        val endpoint = Endpoint(provider.get, url.get, 5, userId, false)//TODO : What about time ??
        val actor = system.actorOf(Props(new ProviderActor(channel, endpoint)))
        system.eventStream.subscribe(actor, classOf[Dead])
        system.eventStream.subscribe(actor, classOf[Ping])
      } else {
        Logger.error("Provider or Url not found for "+unifiedRequest.service+" :: args = "+unifiedRequest.args)
      }
    }
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