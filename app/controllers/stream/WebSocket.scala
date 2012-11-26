package controllers.stream

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors.UserInfosActor
import services.commands.Commands
import play.api.PlayException

object WebSocket extends Controller {

  val log = Logger(WebSocket.getClass())
  
  def connect() = play.api.mvc.WebSocket.using[JsValue] { implicit request =>
    
    val userId = request.session.get("id").getOrElse(throw new PlayException("Security Runtime Error", "Unallowed websocket connection"))

    val (out, channelClient) = Concurrent.broadcast[JsValue]
    UserInfosActor.create(userId, channelClient)

    val in = Iteratee.foreach[JsValue]{ cmd =>
      log.info("Command from client : "+cmd)
      Commands.interpret(userId, cmd)
    }.mapDone { _ =>
      log.info("Disconnected")
      UserInfosActor.killActorsForUser(userId)
    }

    (in, out)
  }

}