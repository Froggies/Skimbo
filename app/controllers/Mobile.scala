package controllers

import play.api.mvc.Controller
import java.util.UUID
import models.user.SkimboToken
import services.endpoints.Endpoints
import views.html.defaultpages.badRequest
import services.commands.CmdFromUser
import models.command.NewToken
import services.actors.UserInfosActor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.mvc.Action
import play.api.Logger
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import services.commands.CmdToUser
import services.actors.PingActor
import play.api.libs.Comet
import play.api.libs.EventSource
import services.auth.ProviderDispatcher
import play.api.mvc.Call
import play.api.mvc.Cookie

object Mobile extends Controller {

  def end() = Action { implicit request =>
    val token = session.get("id").getOrElse("Error in id !")
    Ok(views.html.mobileEndAuthentication(token)).withCookies(
      Cookie("tokenSkimbo", token, httpOnly=false)
    )
  }
  
  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider => 
      provider.auth(routes.Mobile.end).withCookies(Cookie("isMobile", "true")))
    .getOrElse(BadRequest)
  }

  def connect(idUser: String) = Action {
    Logger("Mobile").info("MOBILE ==> " + idUser)
    val (out, channelClient) = Concurrent.broadcast[JsValue]

    CmdToUser.userConnected(idUser, channelClient).map { preferedChannel =>
      UserInfosActor.create(idUser)
      PingActor.create(idUser, preferedChannel)
      services.dao.UserDao.updateLastUse(idUser)
    }

    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }

  def command(idUser: String) = Action { implicit request =>
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    
    request.body.asJson.map(js => {
      println("####################")
      println(js)
      println("####################")
      CmdFromUser.interpret(idUser, js)
    })
    PingActor.ping(idUser)
    Ok("ok")
  }

}