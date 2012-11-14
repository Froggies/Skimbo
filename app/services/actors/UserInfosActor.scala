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
import models.user.ProviderUser
import scala.concurrent._
import scala.annotation.tailrec
import akka.util.Duration
import services.auth.ProviderDispatcher
import services.auth.GenericProvider
import akka.dispatch.OnSuccess
import models.Account
import java.util.Date
import services.endpoints.JsonRequest._
import models.user.Column

case object Retreive
case class Send(userId: String, json: JsValue)
case class StartProvider(userId: String, column: Column)
case class KillProvider(userId: String, columnTitle: String)
case class CheckAccounts(user: User)
case class AddInfosUser(user: User, providerUser:ProviderUser)

object UserInfosActor {

  val system: ActorSystem = ActorSystem("userInfos");

  def create(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) = {
    val actor = system.actorOf(Props(new UserInfosActor(idUser, channelOut)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[Send])
    system.eventStream.subscribe(actor, classOf[StartProvider])
    system.eventStream.subscribe(actor, classOf[KillProvider])
    actor ! Retreive
    actor
  }

  def sendTo(userId: String, json: JsValue) = {
    system.eventStream.publish(Send(userId, json))
  }

  def startProfiderFor(userId: String, column: Column) = {
    system.eventStream.publish(StartProvider(userId, column))
  }

  def killProfiderFor(userId: String, columnTitle: String) = {
    system.eventStream.publish(KillProvider(userId, columnTitle))
  }

  def killActorsForUser(userId: String) = {
    system.eventStream.publish(Dead(userId))
  }

}

class UserInfosActor(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global
  val log = Logger(classOf[UserInfosActor])

  def receive() = {
    case Retreive => {
      log.info("Start retreive")
      UserDao.findOneById(idUser).map { optionUser =>
        if (optionUser.isDefined) {
          log.info("User has id in DB")
          start(optionUser.get)
          self ! CheckAccounts(optionUser.get)
        } else {
          ProviderDispatcher.listAll.map { provider =>
            if (provider.hasToken(request)) {
              log.info("Call distantUser on " + provider.name)
              provider.getUser.map { providerUser =>
                if (providerUser.isDefined) {
                  log.info("User has id in " + provider.name + " = " + providerUser.get.id)
                  UserDao.findByIdProvider(provider.name, providerUser.get.id).map { optionUser =>
                    if (optionUser.isDefined) {
                      log.info("User has id of " + provider.name + " in DB = " + optionUser.get.accounts.last.id)
                      val user = optionUser.get
                      UserDao.update(User(
                        user.accounts :+ Account(idUser, new Date()),
                        user.distants,
                        user.columns))
                      start(user)
                    } else {
                      log.info("User hasn't id of " + provider.name + " in DB createIt")
                      UserDao.add(User(
                        Seq(Account(idUser, new Date())),
                        Some(Seq(providerUser.get))))
                    }
                  }
                } else {
                  log.info("User hasn't id in " + provider.name + " ! WTF ?")
                }
              }
            } else {
              log.info("User hasn't token for " + provider.name)
            }
          }
        }
      }
    }
    case StartProvider(id: String, unifiedRequests) => {
      if (id == idUser) {
        ProviderActor.create(channelOut, idUser, unifiedRequests)
      }
    }
    case KillProvider(id: String, columnTitle) => {
      if (id == idUser) {
        ProviderActor.killActorsForUserAndColumn(id, columnTitle)
      }
    }
    case Send(id: String, json: JsValue) => {
      if (id == idUser) {
        channelOut.push(json)
      }
    }
    case Dead(id) => {
      if (idUser == id) {
        ProviderActor.killActorsForUser(idUser)
        context.stop(self)
      }
    }
    case CheckAccounts(user: User) => {
      ProviderDispatcher.listAll.map { provider =>
        if (provider.hasToken(request)) {
          if (!user.distants.exists { _.exists(_.socialType == provider.name) }) {
            provider.getUser.map { providerUser =>
              if (providerUser.isDefined) {
                self ! AddInfosUser(user, ProviderUser(providerUser.get.id, provider.name))
              }
            }
          }
        }
      }
    }
    case AddInfosUser(user: User, providerUser:ProviderUser) => {
      UserDao.update(User(
        user.accounts,
        Some(user.distants.getOrElse(Seq[ProviderUser]()) :+ providerUser),
        user.columns))
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

  def start(user: User) = {
    user.columns.getOrElse(Seq()).foreach { column =>
      self ! StartProvider(idUser, column)
    }
  }

}