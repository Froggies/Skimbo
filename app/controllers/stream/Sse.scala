package controllers.stream

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors._
import play.api.libs.EventSource
import models.command.Command
import play.api.libs.json.Json
import services.security.Authentication
import services.UserDao
import services.commands.CmdFromUser

object Sse extends Controller with Authentication {

  def ping() = Authenticated { user => request =>
    PingActor.ping(user.accounts.last.id)
    Ok("ok")
  }

  def command() = Authenticated { user => implicit request =>
    val userId = user.accounts.head.id
    request.body.asJson.map(js => CmdFromUser.interpret(userId, js))
    PingActor.ping(user.accounts.last.id)
    Ok("ok")
  }

  def connect() = Authenticated { user => implicit request =>
    val userId = user.accounts.head.id
    val (out, channelClient) = Concurrent.broadcast[JsValue]
    
    UserInfosActor.create(userId, channelClient)
    PingActor.create(userId, channelClient)
    UserDao.updateLastUse(userId)
    
    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }

}