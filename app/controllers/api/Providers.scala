package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import models.Service
import play.api.libs.json.Json
import play.api.libs.json.JsArray

object Providers extends Controller {

  def listAll = Action { implicit req =>
    Ok(Service.toJson()).as(play.api.http.ContentTypes.JSON)
  }
  
  def listServices = Action {  implicit req =>
    Ok(Service.toJsonWithUnifiedRequest).as(play.api.http.ContentTypes.JSON)
  }

}