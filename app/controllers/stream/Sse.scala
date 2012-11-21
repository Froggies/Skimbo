package controllers.stream

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue
import play.api.mvc.Controller
import services.actors._
import services.security.AuthenticatedAction.Authenticated
import play.api.libs.EventSource

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