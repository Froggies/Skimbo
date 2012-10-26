package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import com.codahale.jerkson.Json.generate

object Providers extends Controller {

  def listAll = Action { implicit req =>
    val list = ProviderDispatcher.listAll.map { provider => 
      Map("name" -> provider.name, 
          "logo" -> provider.logo, 
          "isConnected" -> provider.hasToken)
    }
    Ok(generate(list))
  }
  
}