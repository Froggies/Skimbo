package services.commands

import models.command.Command
import scala.collection.mutable.HashMap
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import models.command.TokenInvalid
import models.command.Error
import services.dao.UserDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import scala.concurrent.Future
import scala.annotation.tailrec

object CmdToUser {

  private val log = Logger("CmdToUser")
  private val channelUser = new HashMap[String, Seq[Concurrent.Channel[JsValue]]] //IdUser -> List(output)
  private val userToUser = new HashMap[String, String] //same user with 2 accounts connected

  def getNbAccount = {
    channelUser.size
  }

  def getNbChannels = {
    sum(channelUser.values.map(_.size).toList)
  }
  
  def getNbOtherAccount = {
    userToUser.size
  }

  def sum(xs: List[Int]): Int = {
    @tailrec
    def inner(xs: List[Int], accum: Int): Int = {
      xs match {
        case x :: tail => inner(tail, accum + x)
        case Nil => accum
      }
    }
    inner(xs, 0)
  }
  
  def getInternalIdUser(idUser: String): String = {
    userToUser.get(idUser).getOrElse(idUser)
  }

  def userConnected(idUser: String, channel: Concurrent.Channel[JsValue], isPublicPage: Boolean = false):Future[Concurrent.Channel[JsValue]] = {
    val optChannels = channelUser.get(idUser)
    if (optChannels.isDefined) { //user deco/reco or other tab in navigator
      log.info("user deco/reco")
      channelUser.put(idUser, optChannels.get ++ Seq(channel))
      Future(optChannels.get.head)
    } else if(!isPublicPage) {
      UserDao.findOneById(idUser).map(_.map { user =>
        val account = user.accounts.filter(account => channelUser.get(account.id).isDefined).headOption
        if (account.isDefined) { //another account, probably another browser/pc
          log.info("another account")
          val oldChannels = channelUser.get(account.get.id).get
          channelUser.put(account.get.id, oldChannels ++ Seq(channel))
          userToUser.put(idUser, account.get.id)
          oldChannels.head
        } else { //really new user
          log.info("new connexion")
          channelUser.put(idUser, Seq(channel))
          channel
        }
      }.getOrElse {
        channelUser.put(idUser, Seq(channel))
        userToUser.put(idUser, idUser)
        channel
      })
    } else {//in publicPage we create always channel
      Future(channel)
    }
  }

  def userDeco(idUser: String, channel: Concurrent.Channel[JsValue]) = synchronized {
    channelUser.remove(getInternalIdUser(idUser))
    userToUser.remove(getInternalIdUser(idUser))
    userToUser.remove(idUser)
  }

  private def send(idUser: String, msg: JsValue) = {
    channelUser.get(getInternalIdUser(idUser)).map(_.foreach(_.push(msg)))
  }

  def sendTo(idUser: String, cmd: Command) = {
    send(idUser, Json.toJson(cmd))
  }

  def sendTo(idUser: String, cmd: TokenInvalid) = {
    send(idUser, Json.toJson(cmd))
  }

  def sendTo(idUser: String, cmd: Error) = {
    send(idUser, Json.toJson(cmd))
  }

}