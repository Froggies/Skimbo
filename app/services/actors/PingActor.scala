package services.actors

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Cancellable
import akka.actor.Props
import akka.actor.actorRef2Scala
import models.command.Command
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.libs.Akka
import services.commands.CmdToUser

case class Ping(idUser: String)
case class Ask()

/**
 * only in sse mode
 */
object PingActor {

  private val system: ActorSystem = ActorSystem("userInfos");

  def create(idUser: String, channelOut: Concurrent.Channel[JsValue]) = {
    val actor = system.actorOf(Props(new PingActor(idUser)))
    system.eventStream.subscribe(actor, classOf[Ping])
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

  var schedulerKill: Cancellable = null

  def receive = {
    case Ask => {
      CmdToUser.sendTo(idUser, Command("ping"))
      if (schedulerKill == null || schedulerKill.isCancelled) {
        schedulerKill = Akka.system.scheduler.scheduleOnce(60 second) {
          self ! Dead
        }
      }
    }
    case Ping(id) => {
      if (id == idUser) {
        schedulerKill.cancel
      }
    }
    case Dead => {
      CmdToUser.sendTo(idUser, Command("disconnect"))
      UserInfosActor.killActorsForUser(idUser)
      scheduler.cancel()
      context.stop(self)
    }
  }

}