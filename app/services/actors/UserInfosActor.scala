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
import services.auth.GenericProvider

case object Retreive
case class Send(userId: String, json: JsValue)
case class StartProvider(userId: String, column: Column)
case class CheckAccounts(idUser: String)
case class AddInfosUser(user: User, providerUser: ProviderUser)
case class RefreshInfosUser(userId: String, provider: GenericProvider)

object UserInfosActor {

  private val system: ActorSystem = ActorSystem("userInfos");

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

}

class UserInfosActor(idUser: String, channelOut: Concurrent.Channel[JsValue])(implicit request: RequestHeader) extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global
  val log = Logger(classOf[UserInfosActor])
  
  def receive() = {
    case Retreive => {
      UserDao.findOneById(idUser).map { optionUser =>
        optionUser.map(user => start(user))
          .getOrElse(
            ProviderDispatcher.listAll.map(provider =>
              provider.getToken.map(token =>
                provider.getUser.map(providerUser =>
                  providerUser.map(pUser =>
                    UserDao.findByIdProvider(provider.name, pUser.id).map(optUser =>
                      optUser.map { user =>
                        UserDao.addAccount(user, Account(idUser, new Date()))
                        start(user)
                      }.getOrElse {
                        val user = User(Seq(Account(idUser, new Date())), Some(Seq(pUser)))
                        UserDao.add(user)
                        start(user)
                      })).getOrElse(log.error("User hasn't id in " + provider.name + " ! WTF ?")))).getOrElse(log.info("User hasn't token for " + provider.name))))
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
            if (provider.hasToken(request)) {
              provider.getUser.map { providerUser =>
                if (providerUser.isDefined && !user.distants.exists {
                  _.exists { pu =>
                    pu.socialType == provider.name && pu.id == providerUser.get.id
                  }
                }) {
                  self ! AddInfosUser(user, ProviderUser(providerUser.get.id, provider.name, None)) //TODO put good token
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
    case AddInfosUser(user: User, providerUser: ProviderUser) => {
      UserDao.addProviderUser(user, providerUser)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

  def start(user: User) = {
    if (user.columns.map(_.isEmpty).getOrElse(true)) {
      //new user : check if have another account
      ProviderDispatcher.listAll.map { provider =>
        provider.getToken.map { token =>
          provider.getUser.map { providerUser =>
            providerUser.map(pUser =>
              UserDao.findByIdProvider(provider.name, pUser.id).map(optUser =>
                optUser.map { originalUser =>
                  UserDao.merge(user, originalUser).map { _ =>
                    Commands.interpretCmd(idUser, Command("allColumns"))
                    originalUser.columns.map(_.foreach(self ! StartProvider(idUser, _)))
                  }
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