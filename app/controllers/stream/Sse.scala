package controllers.stream

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors._
import play.api.libs.EventSource
import services.commands.Commands
import models.command.Command
import play.api.libs.json.Json
import services.security.Authentication
import play.api.libs.iteratee.Enumerator
import play.api.Logger
import play.api.libs.iteratee.Input
import scala.concurrent.Channel

object Sse extends Controller with Authentication {

  val log = Logger(Sse.getClass())
  
  def ping() = Authenticated { user => request =>
    ProviderActor.ping(user.accounts.last.id)
    Ok("ok")
  }

  def command() = Authenticated { user => implicit request =>
    val userId = user.accounts.head.id
    request.body.asJson.map(js => Commands.interpret(userId, js))
    Ok("ok")
  }

  def connect() = Authenticated { user => implicit request =>
    val (out, channelClient) = Concurrent.broadcast[JsValue]
    UserInfosActor.create(user.accounts.last.id, channelClient)
    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }

}