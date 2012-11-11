package controllers.stream
import play.api.mvc.Controller
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import services.actors.ProviderActor
import services.auth.providers._
import services.security.AuthenticatedAction._
import play.api.mvc.AnyContent
import play.api.libs.json.JsValue
import play.api.Logger
import services.endpoints.Endpoints
import services.endpoints.JsonRequest._
import services.actors.UserInfosActor
import play.api.libs.iteratee.Concurrent
import services.commands.Commands

object WebSocket extends Controller {

  val log = Logger(WebSocket.getClass())
  
  def connect() = play.api.mvc.WebSocket.using[JsValue] { implicit request =>
    import play.api.libs.concurrent.execution.defaultContext
    
    //TODO : JL Authenticated in WebSocket ??
    val userId = request.session.get("id").get

    val (out, channelClient) = Concurrent.broadcast[JsValue]
    UserInfosActor.create(userId, channelClient)

    val in = Iteratee.foreach[JsValue]{ cmd =>
      log.info("Command from client : "+cmd)
      Commands.interpret(userId, cmd)
    }.mapDone { _ =>
      println("Disconnected")
      ProviderActor.killActorsForUser(userId)
    }

    (in, out)
  }

}