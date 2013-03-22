package services.endpoints

import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Request
import services.auth.GenericProvider
import services.auth.providers._
import services.endpoints.JsonRequest.UnifiedRequest
import services.endpoints.JsonRequest.unifiedRequestReader
import services.auth.RssProvider

case class Endpoint(provider: GenericProvider, services: Seq[EndpointConfig])

object Endpoints {

  // Activated endpoints
  private val endpoints = Seq[Endpoint](
    Endpoint(Twitter, Seq(
      Configuration.Twitter.wall,
      Configuration.Twitter.hashtag,
      Configuration.Twitter.user,
      Configuration.Twitter.directMessage,
      Configuration.Twitter.messageToMe)),
    Endpoint(Facebook, Seq(
      Configuration.Facebook.wall,
      Configuration.Facebook.user,
      Configuration.Facebook.group,
      Configuration.Facebook.message)),
    Endpoint(Viadeo, Seq(
      Configuration.Viadeo.smartNews,
      Configuration.Viadeo.newsfeed)),
    Endpoint(LinkedIn, Seq(
      Configuration.Linkedin.wall)),
    Endpoint(GooglePlus, Seq(
      Configuration.GooglePlus.wall,
      Configuration.GooglePlus.user)),
    Endpoint(GitHub, Seq(
      Configuration.Github.notifications)),
    Endpoint(Trello, Seq(
      Configuration.Trello.notifications)),
    Endpoint(Scoopit, Seq(
      Configuration.Scoopit.wall)),
    Endpoint(BetaSeries, Seq(
      Configuration.BetaSeries.planning,
      Configuration.BetaSeries.timeline)),
    Endpoint(RssProvider, Seq(
      Configuration.Rss.rss))
  )

  def getAll: Seq[Endpoint] = {
    endpoints
  }
  
  def getProvider(service: String): Option[GenericProvider] = {
    val found = endpoints.filter { _.services.filter { _.uniqueName == service }.size > 0 }
    if (found.size > 0) {
      Some(found.head.provider)
    } else {
      None
    }
  }

  def getConfig(service: String): Option[EndpointConfig] = {
    val found = endpoints.flatMap {
      _.services.filter { endServ =>
        endServ.uniqueName == service
      }
    }
    found.headOption
  }

  def genererUrl(endpoint: String, param: Map[String, String], sinceOpt: Option[String]): Option[String] = {
    getConfig(endpoint).flatMap { config =>
      val isRequestValid = config.requiredParams.forall(param.get(_).isDefined) // Check if all required params are defined
      if (!isRequestValid) {
        Logger.error("Request invalid : all required params are not defined.")
        Logger.warn("Given : " + param)
        Logger.warn("Expected : " + config.requiredParams)
        None
      } else {
        // Generate url with params
        val baseUrl = param.foldLeft(config.url)((url, param) => url.replace(":" + param._1, param._2))
        // And "since" element to Url
        Some(
            sinceOpt.map(since => 
              baseUrl + config.since.get.replace(":since", since))
            .getOrElse(baseUrl))
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