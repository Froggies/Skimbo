package controllers

import play.api.libs.EventSource
import play.api.mvc._
import services.actors.ProviderActor
import services.auth.ProviderDispatcher
import services.auth.providers._
import services.actors.Endpoint
import models.Service
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.JsValue

object Application extends Controller {

  def index = Action { implicit request =>
    if(ProviderDispatcher.atLeastOneIsConnected(request)) {
      Ok(views.html.unified())
    } else {
      Ok(views.html.index(Service.list))
    }
  }

  def authenticate(providerName: String) = Action { implicit request =>
    ProviderDispatcher(providerName).map(provider =>
      provider.auth(routes.Application.index)).getOrElse(BadRequest)
  }
  
  def testActor2() = Action { implicit request =>

    val endpoints = Seq(
      Endpoint(Twitter, "http://dev.studio-dev.fr/test-ws-json.php?nom=twitter", 2),
      Endpoint(Facebook, "http://dev.studio-dev.fr/test-ws-json.php?nom=facebook", 5),
      Endpoint(Viadeo, "http://dev.studio-dev.fr/test-ws-json.php?nom=viadeo", 3))

    val enumerator = ProviderActor(endpoints);
    // -> to Skimbo 
    // -> filter en fonction des déjà vus
    // -> to Json
    Ok.feed(enumerator &> EventSource()).as("text/event-stream")
  }
  
  def killAllActor() = Action {
    
    Ok("ok")
  }

}