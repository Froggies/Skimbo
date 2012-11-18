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

  def testRes(providerName: String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    ProviderDispatcher(providerName).map(provider =>
      Async {
        provider.fetch("https://api.viadeo.com/me").get.map { r =>
          val mapper = new ObjectMapper();
          Ok(r.json)
        }
      }).getOrElse(BadRequest)
  }

  def logout() = Authenticated { action =>
    Logger.info("Aplication.scala :: KillMyActors :: " + action.user.accounts.last.id)
    UserInfosActor.killActorsForUser(action.user.accounts.last.id)
    //TODO remove session id
    Ok(views.html.index(Service.list(action.request)))
  }

}