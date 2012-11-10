package services.actors

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{ Concurrent, Enumerator }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.UnexpectedException
import play.api.Logger
import controllers.UserDao
import models.User
import play.api.libs.iteratee.Iteratee
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.bson._
import scala.collection.mutable.ListBuffer
import models.ProviderUser
import scala.concurrent._
import scala.annotation.tailrec
import akka.util.Duration
import services.auth.ProviderDispatcher
import services.auth.GenericProvider
import akka.dispatch.OnSuccess
import models.Account
import java.util.Date
import services.endpoints.JsonRequest._

case object Retreive
case class Send(userId:String, json:JsValue)
case class StartProvider(unifiedRequests:Seq[UnifiedRequest])

object UserInfosActor {

  val system: ActorSystem = ActorSystem("userInfos");

  def create(idUser: String, channelOut:Concurrent.Channel[JsValue])(implicit request: RequestHeader) = {
    val actor = system.actorOf(Props(new UserInfosActor(idUser, channelOut)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[Send])
    actor ! Retreive
    actor
  }
  
  def sendTo(userId:String, json:JsValue) = {
    system.eventStream.publish(Send(userId, json))
  }
  
}

class UserInfosActor(idUser: String, channelOut:Concurrent.Channel[JsValue])(implicit request: RequestHeader) extends Actor {

  import play.api.libs.concurrent.execution.defaultContext

  def receive() = {
    case Retreive => {

      UserDao.findOneById(idUser).map { optionUser =>
        if (optionUser.isDefined) {
          Logger.info("User has id in DB")
        } else {
          ProviderDispatcher.listAll.map { provider =>
            if (provider.hasToken(request)) {
              Logger.info("Call distantUser on " + provider.name)
              provider.getUser.map { providerUser =>
                if (providerUser.isDefined) {
                  Logger.info("User has id in " + provider.name + " = " + providerUser.get.id)
                  UserDao.findByIdProvider(provider.name, providerUser.get.id).map { optionUser =>
                    if (optionUser.isDefined) {
                      Logger.info("User has id of " + provider.name + " in DB = " + optionUser.get.accounts.last.id)
                      request.session + ("id" -> optionUser.get.accounts.last.id)
                      val user = optionUser.get
                      UserDao.update(User(
                        user.accounts :+ Account(idUser, new Date()),
                        user.distants,
                        user.columns))
                      user.columns.getOrElse(Seq()).foreach { column =>
                        self ! StartProvider(column.unifiedRequests)
                      }
                    } else {
                      Logger.info("User hasn't id of " + provider.name + " in DB createIt")
                      UserDao.add(User(
                        Seq(Account(idUser, new Date())),
                        Some(Seq(providerUser.get))))
                    }
                  }
                } else {
                  Logger.info("User hasn't id in " + provider.name + " ! WTF ?")
                }
              }
            } else {
              Logger.info("User hasn't token for " + provider.name)
            }
          }
        }
      }

      // TODO RM : find when kill actor
      //. onDone {
      //  self ! Dead
      //}
    }
    case StartProvider(unifiedRequests) => {
      ProviderActor.create(channelOut, idUser, unifiedRequests)
    }
    case Send(id:String, json:JsValue) => {
      if(id == idUser) {
        channelOut.push(json)
      }
    }
    case Dead => {
      context.stop(self)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

}