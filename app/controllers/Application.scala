package controllers

import models.Service
import play.api.Logger
import play.api.mvc._
import services.actors.UserInfosActor
import services.auth.ProviderDispatcher
import views.html._
import services.auth.providers._
import models.User
import views.html.helper.javascriptRouter
import play.api.Routes

object Application extends Controller {

  def index = Action { implicit request =>
    request.session.get("id")
      .map(_ => Ok(views.html.unified()))
      .getOrElse(Ok(views.html.index(Service.list)))
  }

  def authenticate(providerName: String) = Action { implicit request =>
    val providerOpt  = ProviderDispatcher(providerName);
    val userOpt = request.session.get("id").map(User.create)

    providerOpt.map(provider => 
      userOpt.map(_ => provider.auth(routes.Application.closePopup))
      .getOrElse(provider.auth(routes.Application.index)))
    .getOrElse(BadRequest)
  }

  def logout() = Action { implicit request =>
    Ok(views.html.index(Service.list(request))).withNewSession
  }
  
  def closePopup() = Action {
    Ok(views.html.popupEndAuthentication())
  }
  
  def jsRouter() = Action { implicit request =>
    import routes.javascript._
    import controllers.stream.javascript._

    Ok(
      Routes.javascriptRouter("jsRoutes")(
       routes.javascript.Application.index,
       routes.javascript.Application.authenticate,
       routes.javascript.Application.logout,
       stream.routes.javascript.WebSocket.connect,
       stream.routes.javascript.Sse.connect,
       stream.routes.javascript.Sse.ping   
    )).as("text/javascript")
  }

}