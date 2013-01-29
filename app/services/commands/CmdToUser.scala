package services.commands

import models.command.Command
import scala.collection.mutable.HashMap
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import models.command.TokenInvalid
import models.command.Error
import services.UserDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object CmdToUser {
  
  private val channelUser = new HashMap[String, Seq[Concurrent.Channel[JsValue]]]
  
  def userConnected(idUser: String, channel:Concurrent.Channel[JsValue]) = {
    val optChannels = channelUser.get(idUser)
    if(optChannels.isDefined) {//user deco/reco or other tab in navigator
      channelUser.put(idUser, channelUser.get(idUser).get ++ Seq(channel)) 
    } else {
      UserDao.findOneById(idUser).map( _.map { user =>
        val account = user.accounts.filter( account => channelUser.get(account.id).isDefined ).headOption
        if(account.isDefined) {//another account, probably another browser
          channelUser.put(account.get.id, channelUser.get(account.get.id).get ++ Seq(channel))
        } else {//really new user
          channelUser.put(idUser, Seq(channel))
        }
      })
    }
  }
  
  def userDeco(idUser: String, channel:Concurrent.Channel[JsValue]) = {
    channelUser.get(idUser).map { channels =>
      val newChannels = channels.filterNot( _ == channel)
      if(newChannels.isEmpty) {
        channelUser.remove(idUser)
      } else {
        channelUser.put(idUser, newChannels)
      }
    }
  }
  
  private def send(idUser: String, msg: JsValue) = {
    channelUser.get(idUser).map(_.foreach(_.push(msg)))
  }

  def sendTo(idUser: String, cmd:Command) = {
    send(idUser, Json.toJson(cmd))
  }
  
  def sendTo(idUser: String, cmd:TokenInvalid) = {
    send(idUser, Json.toJson(cmd))
  }
  
  def sendTo(idUser: String, cmd:Error) = {
    send(idUser, Json.toJson(cmd))
  }
  
}