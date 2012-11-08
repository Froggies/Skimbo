package controllers.api

import play.api.mvc._
import services.auth.ProviderDispatcher
import services.endpoints.Endpoints
import services.endpoints.Endpoints

object Unified extends Controller {
  
  import services.endpoints.JsonRequest._

  /*
   * Controleur de débug
   */
  def stream = Action { implicit req => 
    val channels = Endpoints.listEndpointsFromRequest(req)
    
    // What is done ?
    val endpoints = channels.flatMap{channel => 
      val urlOpt = Endpoints.genererUrl(channel.service, channel.args.getOrElse(Map.empty), None)
      val statut = Endpoints.endpoints.get(channel.service).get.provider.getToken.map(_ => "Connecté").getOrElse("Non Connecté")
      urlOpt.map(url => url + " / Statut : " + statut)
    }
      
    Ok(endpoints.mkString("\n"))
  }
  

}
