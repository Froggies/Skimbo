package controllers.stream

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors.UserInfosActor
import play.api.PlayException
import services.UserDao
import services.commands.CmdFromUser
import services.commands.CmdToUser

object WebSocket extends Controller {

  val log = Logger(WebSocket.getClass())
  
  def connect() = play.api.mvc.WebSocket.using[JsValue] { implicit request =>
    
    val idUser = request.session.get("id").getOrElse(throw new PlayException("Security Runtime Error", "Unallowed websocket connection"))

    val (out, channelClient) = Concurrent.broadcast[JsValue]
    
    CmdToUser.userConnected(idUser, channelClient)
    UserInfosActor.create(idUser)
    UserDao.updateLastUse(idUser)
    
    val in = Iteratee.foreach[JsValue](cmd => CmdFromUser.interpret(idUser, cmd))
              .mapDone( _ => UserInfosActor.killActorsForUser(idUser))

    (in, out)
  }

}