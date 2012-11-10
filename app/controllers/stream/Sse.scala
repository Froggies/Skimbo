package controllers.stream

import services.security.AuthenticatedAction._
import services.actors.ProviderActor
import services.auth.providers._
import play.api.libs.EventSource
import play.api.mvc.Controller
import services.endpoints.Endpoints
import play.api.Logger
import play.api.libs.iteratee.Enumerator
import services.endpoints.JsonRequest.UnifiedRequest
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import services.actors.UserInfosActor

object Sse extends Controller {

  def ping() = Authenticated { action =>
    ProviderActor.ping(action.user.accounts.last.id)
    Ok("ok")
  }

  def connect() = Authenticated { action =>
    implicit val request = action.request

    val (out, channelClient) = Concurrent.broadcast[JsValue]
    UserInfosActor.create(action.user.accounts.last.id, channelClient)
    // -> to Skimbo
    // -> trier par date
    // -> filter en fonction des déjà vus
    // -> to Json
    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }

}