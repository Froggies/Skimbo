package services.actors

import java.util.Date

import scala.concurrent.ExecutionContext.Implicits._

import akka.actor._
import controllers.UserDao
import models.command.Command
import models.User
import models.user._
import play.api.Logger
import play.api.UnexpectedException
import play.api.libs.iteratee.Concurrent
import play.api.libs.json._
import play.api.mvc.RequestHeader
import services.auth.ProviderDispatcher
import services.commands.Commands

case object Retreive
case class Send(userId: String, json: JsValue)
case class StartProvider(userId: String, column: Column)
case class KillProvider(userId: String, columnTitle: String)
case class CheckAccounts(user: User)
case class AddInfosUser(user: User, providerUser: ProviderUser)

object UserInfosActor {

  val system: ActorSystem = ActorSystem("userInfos");

  def create(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) = {
    val actor = system.actorOf(Props(new UserInfosActor(idUser, channelOut)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[Send])
    system.eventStream.subscribe(actor, classOf[StartProvider])
    system.eventStream.subscribe(actor, classOf[KillProvider])
    system.eventStream.subscribe(actor, classOf[Dead])
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
                      UserDao.addAccount(user, Account(idUser, new Date()))
                      start(user)
                    } else {
                      log.info("User hasn't id of " + provider.name + " in DB createIt")
                      val user = User(
                        Seq(Account(idUser, new Date())),
                        Some(Seq(providerUser.get)))
                      UserDao.add(user)
                      start(user)
                    }
                  }
                } else {
                  log.error("User hasn't id in " + provider.name + " ! WTF ?")
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
          provider.getUser.map { providerUser =>
            if (providerUser.isDefined && !user.distants.exists { _.exists(_.socialType == provider.name) }) {
              self ! AddInfosUser(user, ProviderUser(providerUser.get.id, provider.name))
            }
            if (providerUser.isDefined) {
              self ! Send(idUser, Json.toJson(Command("userInfos", Some(Json.toJson(providerUser.get)))))
            }
          }
        }
      }
    }
    case AddInfosUser(user: User, providerUser: ProviderUser) => {
      UserDao.addProviderUser(user, providerUser)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

  def start(user: User) = {
    println("START USER userInfosActor "+user.columns)
    user.columns.getOrElse(Seq()).foreach { column =>
      self ! StartProvider(idUser, column)
    }
    Commands.interpretCmd(idUser, Command("allColumns"))
    self ! CheckAccounts(user)
  }

}