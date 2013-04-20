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
import play.api.http.ContentTypes._
import services.dao.PublicPageDao
import play.api.libs.ws.WS
import java.io.File
import java.net.URI
import java.io.FileOutputStream
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.iteratee.Enumerator
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URLDecoder
import java.io.FileInputStream
import scalax.file.FileOps
import scalax.file.Path
import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStream
import java.io.BufferedOutputStream

object Application extends Controller {

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

    providerOpt.map(provider => 
      userOpt.map(_ => provider.auth(routes.Application.closePopup))
      .getOrElse(provider.auth(routes.Application.index))) 
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
       stream.routes.javascript.Sse.ping   
    )).as(JAVASCRIPT)
  }
  
  def downloadDistant(url: String) = Action {
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
  }

}