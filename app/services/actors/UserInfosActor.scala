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
import services.dao.UserDao
import services.auth.GenericProvider
import services.auth.ProviderDispatcher
import services.auth.AuthProvider
import models.command.Error
import services.commands.CmdFromUser
import services.commands.CmdToUser
import scala.collection.mutable.HashMap

case object Retreive
case class StartProvider(userId: String, column: Column)
case class CheckAccounts(idUser: String)
case class AddInfosUser(providerUser: ProviderUser)
case class RefreshInfosUser(userId: String, provider: GenericProvider)

object UserInfosActor {
  
  def create(idUser: String)(implicit request: RequestHeader) = {
    HelperUserInfosActor.foundOrCreate(idUser)
  }

  def startProfiderFor(userId: String, column: Column) = {
    HelperUserInfosActor.system.eventStream.publish(StartProvider(userId, column))
  }

  def killActorsForUser(userId: String) = {
    HelperUserInfosActor.system.eventStream.publish(Dead(userId))
  }

  def refreshInfosUser(userId: String, provider: GenericProvider) = {
    HelperUserInfosActor.system.eventStream.publish(RefreshInfosUser(userId, provider))
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

class UserInfosActor(idUser: String)(implicit request: RequestHeader) extends Actor {

  val log = Logger(classOf[UserInfosActor])
  
  def getIdUser = idUser
  
  override def preStart() = {
    self ! Retreive
  }

  def receive() = {
    case Retreive => {
      log.info("Retreive")
      UserDao.findOneById(idUser).map { optionUser =>
        optionUser
          .map(user => start(user))
          .getOrElse {
            log.error("user haven't id in bd, WTF !?")
            CmdToUser.sendTo(idUser, Command("disconnect"))
            self ! Dead(idUser)
          }
      }
    }
    case StartProvider(id: String, unifiedRequests) => {
      if (id == idUser) {
        ProviderActor.create(idUser, unifiedRequests)
        self ! CheckAccounts(id)
      }
    }
    case Dead(id) => {
      if (idUser == id) {
        ProviderActor.killActorsForUser(idUser)
        HelperUserInfosActor.delete(idUser)
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
                  CmdToUser.sendTo(idUser, Command("userInfos", Some(Json.toJson(providerUser.get))))
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
        CmdFromUser.interpretCmd(idUser, Command("allColumns"))
      } else {
        providers.foreach { provider =>
          provider.getUser.map { providerUser =>
            providerUser.map(pUser =>
              UserDao.findByIdProvider(provider.name, pUser.id).map(optUser =>
                optUser.map { originalUser =>
                  UserDao.merge(idUser, originalUser.accounts.head.id, {
                    CmdFromUser.interpretCmd(idUser, Command("allColumns"))
                    originalUser.columns.map(_.foreach(self ! StartProvider(idUser, _)))
                    CmdToUser.sendTo(idUser, Command("userInfos", Some(Json.toJson(providerUser.get))))
                  })
                }.getOrElse {
                  CmdFromUser.interpretCmd(idUser, Command("allColumns"))
                  self ! CheckAccounts(idUser)
                }))
          }
        }
      }
    } else {
      user.columns.map(_.foreach(self ! StartProvider(idUser, _)))
      CmdFromUser.interpretCmd(idUser, Command("allColumns"))
    }
  }

}