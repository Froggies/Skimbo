package services.actors

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.UnexpectedException
import play.api.Logger
import controllers.UserDao
import models.User

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
      Logger.info("actor user infos pull " + endpoint.provider.name + " on " + endpoint.url)
      println(endpoint.provider.getUser)
      //get in bd if user exist
      //if exist check provider info and update
      //else create
      import scala.concurrent.ExecutionContext.Implicits.global
      UserDao.add(User(endpoint.idUser)).onComplete { l =>
        UserDao.findAll().foreach {
          user => {
            println("UserInfosActor :: "+user)
            //self ! Dead
          }
        }
        println(l)
      }
    }
    case Dead => {
      Logger.info("actor user infos kill")
      context.stop(self)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }
  
}