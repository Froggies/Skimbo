package controllers

import play.api.libs.EventSource
import play.api.mvc._
import services.actors.ProviderActor
import services.auth.ProviderDispatcher
import services.auth.providers._
import models.Service
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import scala.concurrent.util.duration._
import services.security.AuthenticatedAction._
import play.api.Logger
import services.actors.UserInfosActor
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.PrettyPrinter
import play.api.libs.json.JsValue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import play.api.libs.json.Json

object Application extends Controller {

  def index = Action { implicit request =>
    request.session.get("id").map(userId => {
      Ok(views.html.unified())
    }).getOrElse(Ok(views.html.index(Service.list)))
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)).getOrElse(BadRequest)
  }
  
  def testRes(since:String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
      Async {
        GooglePlus.fetch("https://www.googleapis.com/plus/v1/people/me/activities/public?maxResults=30").get.map { r =>
          val mapper = new ObjectMapper();
          Ok(r.json)
        }
      }
  }

  def logout() = Authenticated { action =>
    Logger.info("Aplication.scala :: KillMyActors :: " + action.user.accounts.last.id)
    UserInfosActor.killActorsForUser(action.user.accounts.last.id)
    //TODO remove session id
    Ok(views.html.index(Service.list(action.request)))
  }

}