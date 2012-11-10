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

object WebSocket extends Controller {

  def connect() = play.api.mvc.WebSocket.using[JsValue] { implicit request =>
    //TODO : JL Authenticated in WebSocket ??
    val userId = request.session.get("id").get

    val (out, channelClient) = ProviderActor.create(userId, Seq[UnifiedRequest]())//endpoints)

    // Log events to the console
    val in = Iteratee.foreach[JsValue]{ cmd =>
      Logger.info("Command from client : "+cmd)
//      val unifiedRequests = Endpoints.listEndpointsFromJson(cmd)
//      ProviderActor.create(channelClient, userId, unifiedRequests)

      ProviderActor.launchAll(channelClient, userId)

    }.mapDone { _ =>
      println("Disconnected")
      ProviderActor.killActorsForUser(userId)
    }

    (in, out)
  }

}