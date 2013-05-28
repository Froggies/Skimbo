package controllers

import java.io.File
import java.net.URLDecoder
import scala.concurrent.ExecutionContext.Implicits.global
import models.Service
import models.User
import play.api.Routes
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.mvc.Controller
import services.auth.ProviderDispatcher
import services.commands.DelayedPostPolling
import play.api.mvc.DiscardingCookie

object Application extends Controller {

  //start polling delayed
  private val pollDelayedPost = DelayedPostPolling
  
  def index = Action { implicit request =>
    session.get("id")
      .map(_ => Ok(views.html.unified()))
      .getOrElse(Ok(views.html.index(Service.list)))
  }
  
  def publicPage(namePage: String) = Action { implicit request =>
    Ok(views.html.unified())
  }

  def authenticate(providerName: String) = Action { implicit request =>
    val providerOpt  = ProviderDispatcher(providerName);
    val userOpt = session.get("id").map(User.create)
    val isMobile = request.cookies.get("isMobile").map(_.value)

    providerOpt.map(provider => 
      userOpt.map(_ => isMobile.map(_ => provider.auth(routes.Mobile.end).discardingCookies(DiscardingCookie("isMobile")))
          .getOrElse(provider.auth(routes.Application.closePopup)))
      .getOrElse(
          isMobile.map(_ => provider.auth(routes.Mobile.end).discardingCookies(DiscardingCookie("isMobile")))
          .getOrElse(provider.auth(routes.Application.index))))
    .getOrElse(BadRequest)
  }

  def logout() = Action {
    Redirect(routes.Application.index).withNewSession
  }
  
  def closePopup() = Action {
    Ok(views.html.popupEndAuthentication())
  }
  
  def jsRouter() = Action { implicit request =>
    import routes.javascript._
    import controllers.stream.javascript._

    Ok(
      Routes.javascriptRouter("jsRoutes")(
       stream.routes.javascript.WebSocket.connect,
       stream.routes.javascript.Sse.connect,
       stream.routes.javascript.Sse.ping,
       stream.routes.javascript.LongPolling.connect
    )).as(JAVASCRIPT)
  }
  
  def downloadDistant = Action { implicit request =>
    request.getQueryString("url").map { url =>
      Async {
        WS.url(URLDecoder.decode(url, "utf-8")).get.map { file =>
          if(file.status == 200 && file.body.size > 0) {
            Ok(file.getAHCResponse.getResponseBodyAsBytes)
              .as(file.header("Content-Type").getOrElse("image/png"))
          } else {
            Ok.sendFile(new File("public/img/image-default.png"))
          }
        }
      }
    } getOrElse (Ok.sendFile(new File("public/img/image-default.png")))
  }

}