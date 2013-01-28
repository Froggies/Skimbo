package services.commands

import models.command.Command
import scala.collection.mutable.HashMap
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import models.command.TokenInvalid
import models.command.Error

object CmdToUser {
  
  private val channelUser = new HashMap[String, Concurrent.Channel[JsValue]]
  
  def userConnected(idUser: String, channel:Concurrent.Channel[JsValue]) = {
    channelUser.put(idUser, channel)
  }
  
  def userDeco(idUser: String) = {
    channelUser.remove(idUser)
  }

  def sendTo(idUser: String, cmd:Command) = {
    channelUser.get(idUser).map(_.push(Json.toJson(cmd)))
  }
  
  def sendTo(idUser: String, cmd:TokenInvalid) = {
    channelUser.get(idUser).map(_.push(Json.toJson(cmd)))
  }
  
  def sendTo(idUser: String, cmd:Error) = {
    channelUser.get(idUser).map(_.push(Json.toJson(cmd)))
  }
  
}