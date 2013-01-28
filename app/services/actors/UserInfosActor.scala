package services.actors

import java.util.Date
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor._
import models.User
import models.command.Command
import models.user._
import play.api.Logger
import play.api.UnexpectedException
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import services.UserDao
import services.auth.GenericProvider
import services.auth.ProviderDispatcher
import services.commands.Commands
import services.auth.AuthProvider
import models.command.Error

case object Retreive
case class Send(userId: String, json: JsValue)
case class StartProvider(userId: String, column: Column)
case class CheckAccounts(idUser: String)
case class AddInfosUser(providerUser: ProviderUser)
case class RefreshInfosUser(userId: String, provider: GenericProvider)

object UserInfosActor {

  private val system = ActorSystem("userInfos")

  def create(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) = {
    val actor = system.actorOf(Props(new UserInfosActor(idUser, channelOut)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[Send])
    system.eventStream.subscribe(actor, classOf[StartProvider])
    system.eventStream.subscribe(actor, classOf[Dead])
    system.eventStream.subscribe(actor, classOf[RefreshInfosUser])
    actor ! Retreive
    actor
  }

  def sendTo(userId: String, json: JsValue) = {
    system.eventStream.publish(Send(userId, json))
  }

  def startProfiderFor(userId: String, column: Column) = {
    system.eventStream.publish(StartProvider(userId, column))
  }

  def killActorsForUser(userId: String) = {
    system.eventStream.publish(Dead(userId))
  }

  def refreshInfosUser(userId: String, provider: GenericProvider) = {
    system.eventStream.publish(RefreshInfosUser(userId, provider))
  }

  def restartProviderColumns(userId: String, provider: GenericProvider) = {
    UserDao.findOneById(userId).map(_.map { user =>
      user.columns.map(
        _.filter(
          _.unifiedRequests.exists(
            _.service.startsWith(provider.name))).foreach(startProfiderFor(userId, _)))
    })
  }

}

class UserInfosActor(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global
  val log = Logger(classOf[UserInfosActor])

  def receive() = {
    case Retreive => {
      UserDao.findOneById(idUser).map { optionUser =>
        optionUser
          .map(user => start(user))
          .getOrElse {
            log.error("user haven't id in bd, WTF !?")
            channelOut.push(Json.toJson(Command("disconnect")))
            self ! Dead(idUser)
          }
      }
    }
    case StartProvider(id: String, unifiedRequests) => {
      if (id == idUser) {
        ProviderActor.create(channelOut, idUser, unifiedRequests)
        self ! CheckAccounts(id)
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
    case CheckAccounts(idUser: String) => {
      ProviderDispatcher.listAll.map { provider =>
        self ! RefreshInfosUser(idUser, provider)
      }
    }
    case RefreshInfosUser(id: String, provider: GenericProvider) => {
      if (id == idUser) {
        UserDao.findOneById(idUser).map {
          _.map { user =>
            if (provider.isAuthProvider && provider.canStart) {
              (provider.asInstanceOf[AuthProvider]).getUser.map { providerUser =>
                if (providerUser.isDefined && !user.distants.exists(
                  _.exists(pu => pu.socialType == provider.name && pu.id == providerUser.get.id))) {
                  self ! AddInfosUser(ProviderUser(providerUser.get.id, provider.name, None))
                }
                if (providerUser.isDefined) {
                  self ! Send(idUser, Json.toJson(Command("userInfos", Some(Json.toJson(providerUser.get)))))
                }
              }
            }
          }
        }
      }
    }
    case AddInfosUser(providerUser: ProviderUser) => {
      UserDao.addProviderUser(idUser, providerUser)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

  def start(user: User) = {
    if (user.columns.map(_.isEmpty).getOrElse(true)) {
      val providers = ProviderDispatcher.listAll.filter(_.hasToken)
      if (providers.isEmpty) {
        Commands.interpretCmd(idUser, Command("allColumns"))
      } else {
        providers.map { provider =>
          provider.getUser.map { providerUser =>
            providerUser.map(pUser =>
              UserDao.findByIdProvider(provider.name, pUser.id).map(optUser =>
                optUser.map { originalUser =>
                  UserDao.merge(idUser, originalUser.accounts.head.id, {
                    Commands.interpretCmd(idUser, Command("allColumns"))
                    originalUser.columns.map(_.foreach(self ! StartProvider(idUser, _)))
                    self ! Send(idUser, Json.toJson(Command("userInfos", Some(Json.toJson(providerUser.get)))))
                  })
                }.getOrElse {
                  Commands.interpretCmd(idUser, Command("allColumns"))
                  self ! CheckAccounts(idUser)
                }))
          }
        }
      }
    } else {
      user.columns.map(_.foreach(self ! StartProvider(idUser, _)))
      Commands.interpretCmd(idUser, Command("allColumns"))
    }
  }

}