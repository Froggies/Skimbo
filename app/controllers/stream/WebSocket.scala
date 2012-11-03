package controllers.stream
import play.api.mvc.Controller
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import services.actors.{Endpoint, ProviderActor}
import services.auth.providers._
import services.security.AuthenticatedAction._
import play.api.mvc.AnyContent
import play.api.libs.json.JsValue

object WebSocket extends Controller {

  def connect() = play.api.mvc.WebSocket.using[JsValue] { implicit request => 
    //TODO : JL Authenticated in WebSocket ??
    val userId = request.session.get("id").get
    
    //TODO RM : remove when api endpoint from JL was done
    val endpoints = Seq(
      Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 5, userId, false),
      Endpoint(GitHub, "http://dev.studio-dev.fr/test-ws-json.php?nom=github", 10, userId, false),
      Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 15, userId, false),
      Endpoint(GooglePlus, "http://dev.studio-dev.fr/test-ws-json.php?nom=googlePlus", 3, userId, false),
      Endpoint(LinkedIn, "http://dev.studio-dev.fr/test-ws-json.php?nom=linkedIn", 8, userId, false),
      Endpoint(Scoopit, "http://dev.studio-dev.fr/test-ws-json.php?nom=scoopit", 5, userId, false),
      Endpoint(StackExchange, "http://dev.studio-dev.fr/test-ws-json.php?nom=stackExchange", 6, userId, false),
      Endpoint(Trello, "http://dev.studio-dev.fr/test-ws-json.php?nom=trello", 5, userId, false),
      Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 15, userId, false))

    val out = ProviderActor.create(endpoints)
    
    // Log events to the console
    val in = Iteratee.foreach[JsValue](println).mapDone { _ =>
      println("Disconnected")
      ProviderActor.killActorsForUser(userId)
    }

    (in, out)
  }
  
}