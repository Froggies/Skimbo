package services.actors

import scala.concurrent.ExecutionContext.Implicits._
import akka.actor._
import scala.concurrent.duration._
import play.api.libs.iteratee._
import play.api.libs.json.JsValue
import play.libs.Akka
import models.command.Command
import play.api.libs.json.Json

case class Ping(idUser: String)
case class Ask()

/**
 * only in sse mode
 */
object PingActor {

  private val system: ActorSystem = ActorSystem("userInfos");
  
  def create(idUser: String, channelOut: Concurrent.Channel[JsValue]) = {
    val actor = system.actorOf(Props(new PingActor(idUser)))
    actor
  }
  
  def ping(userId: String) = {
    system.eventStream.publish(Ping(userId))
  }
  
}

class PingActor(idUser: String) extends Actor {
  
  val scheduler = Akka.system.scheduler.schedule(0 second, 30 second) {
    self ! Ask
  }
  
  var schedulerKill:Cancellable = null
  
  def receive = {
    case Ask => {
      UserInfosActor.sendTo(idUser, Json.toJson(Command("Ping")))
      if(schedulerKill == null || schedulerKill.isCancelled) {
        schedulerKill = Akka.system.scheduler.scheduleOnce(60 second) {
          self ! Kill
        }
      }
    }
    case Ping(id) => {
      if (id == idUser) {
        schedulerKill.cancel
      }
    }
    case Dead => {
      UserInfosActor.sendTo(idUser, Json.toJson(Command("disconnect")))
      UserInfosActor.killActorsForUser(idUser);
      scheduler.cancel()
      context.stop(self)
    }
  }
  
}