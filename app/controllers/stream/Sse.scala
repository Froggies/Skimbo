package controllers.stream

import services.security.AuthenticatedAction._
import services.actors.{Endpoint, ProviderActor}
import services.auth.providers._
import play.api.libs.EventSource
import play.api.mvc.Controller
import services.endpoints.Endpoints
import play.api.Logger
import play.api.libs.iteratee.Enumerator
import services.endpoints.JsonRequest.UnifiedRequest

object Sse extends Controller {

  def ping() = Authenticated { action =>
    ProviderActor.ping(action.user.id)
    Ok("ok")
  }
  
  def connect() = Authenticated { action =>
    implicit val request = action.request

    val channels = Endpoints.listEndpointsFromRequest(request)
    val endpoints = channels.map[Endpoint, Seq[Endpoint]] { channel =>
          val provider = Endpoints.getProvider(channel.service)
          val url = Endpoints.genererUrl(channel.service, channel.args.getOrElse(Map.empty), None)
          if(provider.isDefined && url.isDefined) {
            Logger.info("Provider : "+provider.get.name)
            Endpoint(provider.get, url.get, 5, action.user.id, true)//TODO : What about time ??
          } else {
            Endpoint(null, null, 1, null, false)//TODO JL : please remove this from RM
          }
        }

    val (out, channelClient) = ProviderActor.create(endpoints)
    // -> to Skimbo 
    // -> trier par date
    // -> filter en fonction des déjà vus
    // -> to Json
    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }
  
}