package services.endpoints

import play.api.Logger
import play.api.mvc._
import services.endpoints.JsonRequest._
import play.api.libs.json.JsValue
import services.auth.GenericProvider
import services.auth.providers._

case class Service(service: String, configuration: EndpointConfig)
case class Endpoint(provider: GenericProvider, services: Seq[Service])

object Endpoints {

  // Activated endpoints
  private val endpoints = Seq[Endpoint](
    Endpoint(Twitter, Seq(
      Service("twitter.wall", Configuration.Twitter.wall),
      Service("twitter.hashtag", Configuration.Twitter.hashtag),
      Service("twitter.user", Configuration.Twitter.user))),
    Endpoint(Facebook, Seq(
      Service("facebook.wall", Configuration.Facebook.wall),
      Service("facebook.user", Configuration.Facebook.user),
      Service("facebook.group", Configuration.Facebook.group))),
    Endpoint(Viadeo, Seq(
      Service("viadeo.wall", Configuration.Viadeo.wall))),
    Endpoint(LinkedIn, Seq(
      Service("linkedin.wall", Configuration.Linkedin.wall))),
    Endpoint(GooglePlus, Seq(
      Service("googleplus.wall", Configuration.GooglePlus.wall))),
    Endpoint(GitHub, Seq(
      Service("github.notifications", Configuration.Github.notifications))),
    Endpoint(Trello, Seq(
      Service("trello.notifications", Configuration.Trello.notifications))),
    Endpoint(Scoopit, Seq(
      Service("scoopit.notifications", Configuration.Scoopit.notifications))))

  def getAll: Seq[Endpoint] = {
    endpoints
  }

  def getProvider(service: String): Option[GenericProvider] = {
    val found = endpoints.filter { _.services.filter { _.service == service }.size > 0 }
    if (found.size > 0) {
      Some(found.head.provider)
    } else {
      None
    }
  }

  def getConfig(service: String): Option[EndpointConfig] = {
    val found = endpoints.flatMap {
      _.services.filter { endServ =>
        endServ.service == service
      }
    }
    if (found.size > 0) {
      Some(found.head.configuration)
    } else {
      None
    }
  }

  def genererUrl(endpoint: String, param: Map[String, String], sinceOpt: Option[String]): Option[String] = {
    getConfig(endpoint).map { config =>
      val isRequestValid = config.requiredParams.forall(param.get(_).isDefined) // Check if all required params are defined
      if (!isRequestValid) {
        Logger.error("Request invalid : all required params are not defined.")
        Logger.warn("Given : " + param)
        Logger.warn("Expected : " + config.requiredParams)
        None
      }

      val baseUrl = param.foldLeft(config.url)((url, param) => url.replace(":" + param._1, param._2)) // Generate url with params
      if(!config.manualNextResults) {
        sinceOpt.map(since => baseUrl.replace(":since", since)).getOrElse(baseUrl) // And "since" element to Url
      } else {
        baseUrl
      }
    }
  }

  def listEndpointsFromRequest(req: Request[AnyContent]): List[UnifiedRequest] = {
    req.body.asJson.map(json =>
      listEndpointsFromJson(json)).getOrElse(List.empty)
  }

  def listEndpointsFromJson(json: JsValue): List[UnifiedRequest] = {
    (json \ ("channels")).as[List[UnifiedRequest]]
  }

}