package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import com.codahale.jerkson.Json.generate
import models.Service

object Providers extends Controller {

  def listAll = Action { implicit req =>
    Ok(generate(Service.list))
  }
  
}