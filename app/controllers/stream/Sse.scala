package controllers.stream

import services.security.AuthenticatedAction._
import services.actors.{Endpoint, ProviderActor}
import services.auth.providers._
import play.api.libs.EventSource
import play.api.mvc.Controller

object Sse extends Controller {

  def ping() = Authenticated { action =>
    ProviderActor.ping(action.user.id)
    Ok("ok")
  }
  
  def connect() = Authenticated { action =>
    implicit val request = action.request

    //TODO RM : remove when api endpoint from JL was done
    val endpoints = Seq(
      Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 5, action.user.id, true),
      Endpoint(GitHub, "http://dev.studio-dev.fr/test-ws-json.php?nom=github", 10, action.user.id, true),
      Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 15, action.user.id, true),
      Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=googlePlus", 3, action.user.id, true),
      Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=linkedIn", 8, action.user.id, true),
      Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=scoopit", 5, action.user.id, true),
      Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=stackExchange", 6, action.user.id, true),
      Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=trello", 5, action.user.id, true),
      Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 15, action.user.id, true))

    val (out, channelClient) = ProviderActor.create(endpoints)
    // -> to Skimbo 
    // -> trier par date
    // -> filter en fonction des déjà vus
    // -> to Json
    Ok.stream(out &> EventSource()).as(play.api.http.ContentTypes.EVENT_STREAM)
  }
  
}