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
import services.auth.GenericProvider
import play.api.cache.Cache
import play.api.Play.current

case object Retreive
case class StartProvider(userId: String, column: Column)
case class AddInfosUser(providerUser: ProviderUser)
case class RefreshInfosUser(userId: String, provider: GenericProvider)

object UserInfosActor {

  def create(idUser: String) = {
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

class UserInfosActor(idUser: String) extends Actor {

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
            CmdToUser.sendTo(idUser, Command("logout"))
            self ! Dead(idUser)
          }
      }
    }
    case StartProvider(id: String, unifiedRequests) => {
      if (id == idUser) {
        ProviderActor.create(idUser, unifiedRequests)
      }
    }
    case Dead(id) => {
      if (idUser == id) {
        ProviderActor.killActorsForUser(idUser)
        HelperUserInfosActor.delete(idUser)
        context.stop(self)
      }
    }
    case RefreshInfosUser(id: String, provider: GenericProvider) => {
      if (id == idUser) {
        UserDao.findOneById(idUser).map {
          _.map { user =>
            if (provider.isAuthProvider && provider.canStart(idUser)) {
              Cache.getAs[ProviderUser](user.accounts.head.id+provider.name).map { providerUser =>
                CmdToUser.sendTo(idUser, Command("userInfos", Some(Json.toJson(providerUser))))
              }.getOrElse {
                (provider.asInstanceOf[AuthProvider]).getUser(idUser).map { providerUser =>
                  if (providerUser.isDefined && !user.distants.exists(
                    _.exists(pu => pu.socialType == provider.name && pu.id == providerUser.get.id))) {
                    self ! AddInfosUser(ProviderUser(providerUser.get.id, provider.name, None))
                  }
                  if (providerUser.isDefined) {
                    Cache.set(user.accounts.head.id+provider.name, providerUser.get)
                    CmdToUser.sendTo(idUser, Command("userInfos", Some(Json.toJson(providerUser.get))))
                  }
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
    val providers = ProviderDispatcher.listAll.filter(_.hasToken(idUser))
    if (user.columns.map(_.isEmpty).getOrElse(true)) {
      val idUser = user.accounts.lastOption.map(_.id).getOrElse("")
      if (providers.isEmpty) {
        CmdFromUser.interpretCmd(idUser, Command("allColumns"))
      } else {
        checkUserByIdProvider(providers)
      }
    } else {
      user.columns.map(_.foreach(self ! StartProvider(idUser, _)))
      providers.foreach(self ! RefreshInfosUser(idUser, _))
      CmdFromUser.interpretCmd(idUser, Command("allColumns"))
    }
  }
  
  def checkUserByIdProvider(providers:Seq[AuthProvider]): Unit = {
    providers.headOption.map {provider =>
      provider.getUser(idUser).map { providerUser =>
        providerUser.map(pUser =>
          UserDao.findByIdProvider(provider.name, pUser.id).map(optUser =>
            optUser.map { originalUser =>
              UserDao.merge(idUser, originalUser.accounts.head.id, {
                CmdFromUser.interpretCmd(idUser, Command("allColumns"))
                originalUser.columns.map(_.foreach(self ! StartProvider(idUser, _)))
                ProviderDispatcher.listAll.filter(_.hasToken(idUser)).foreach(self ! RefreshInfosUser(idUser, _))
              })
            }.getOrElse {//provider id user not found in db
              if(providers.size == 1) {//last provider and user wasn't found
                CmdFromUser.interpretCmd(idUser, Command("allColumns"))
                self ! AddInfosUser(pUser)
                CmdToUser.sendTo(idUser, Command("userInfos", Some(Json.toJson(pUser))))
              } else {//check next provider
                checkUserByIdProvider(providers.drop(1))
              }
            }))
      }
    }
  }

}