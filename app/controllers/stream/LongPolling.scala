package controllers.stream

import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors.PingActor
import services.actors.UserInfosActor
import services.commands.CmdFromUser
import services.commands.CmdToUser
import services.dao.UserDao
import services.security.Authentication
import play.api.libs.Comet

object LongPolling extends Controller with Authentication {

  def command() = Authenticated { user => implicit request =>
    val userId = user.accounts.head.id
    request.body.asJson.map(js => CmdFromUser.interpret(userId, js))
    Ok("ok")
  }

  def connect() = Authenticated { user => implicit request =>
    val idUser = user.accounts.head.id
    val (out, channelClient) = Concurrent.broadcast[JsValue]
    
    CmdToUser.userConnected(idUser, channelClient).map { preferedChannel =>
      UserInfosActor.create(idUser)
      PingActor.create(idUser, preferedChannel)
      UserDao.updateLastUse(idUser)
    }
    
    Ok.stream(out &> Comet(callback = "parent.longPolling"))
  }
  
}