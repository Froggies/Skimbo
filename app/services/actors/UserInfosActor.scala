package services.actors

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.UnexpectedException

case class Refresh()

object UserInfosActor {
  
  val system: ActorSystem = ActorSystem("userInfos");
  
  def apply(endpoint: Endpoint)(implicit request: RequestHeader) = {
    val actor = system.actorOf(Props(new UserInfosActor(endpoint)))
    actor ! Refresh
  }
}

class UserInfosActor(endpoint: Endpoint)(implicit request: RequestHeader) extends Actor {

  def receive = {
    case Refresh => {
      println("actor user infos pull " + endpoint.provider.name + " on " + endpoint.url)
      println(endpoint.provider.getUser)
      //TODO save in bd
    }
    case Dead(idUser) => {
      if (idUser == endpoint.idUser) {
        println("actor user infos kill for " + idUser)
        context.stop(self)
      }
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }
  
}