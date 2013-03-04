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

case class Service(service: String, configuration: EndpointConfig)
case class Endpoint(provider: GenericProvider, services: Seq[Service])

object Endpoints {

  // Activated endpoints
  private val endpoints = Seq[Endpoint](
    Endpoint(Twitter, Seq(
      Service("twitter.wall", Configuration.Twitter.wall),
      Service("twitter.hashtag", Configuration.Twitter.hashtag),
      Service("twitter.user", Configuration.Twitter.user),
      Service("twitter.directMessage", Configuration.Twitter.directMessage),
      Service("twitter.messageToMe", Configuration.Twitter.messageToMe),
      Service("twitter.connect", Configuration.Twitter.connect))),
    Endpoint(Facebook, Seq(
      Service("facebook.wall", Configuration.Facebook.wall),
      Service("facebook.user", Configuration.Facebook.user),
      Service("facebook.group", Configuration.Facebook.group),
      Service("facebook.message", Configuration.Facebook.message))),
    Endpoint(Viadeo, Seq(
      Service("viadeo.smartNews", Configuration.Viadeo.smartNews),
      Service("viadeo.newsfeed", Configuration.Viadeo.newsfeed))),
    Endpoint(LinkedIn, Seq(
      Service("linkedin.wall", Configuration.Linkedin.wall))),
    Endpoint(GooglePlus, Seq(
      Service("googleplus.wall", Configuration.GooglePlus.wall))),
    Endpoint(GitHub, Seq(
      Service("github.notifications", Configuration.Github.notifications))),
    Endpoint(Trello, Seq(
      Service("trello.notifications", Configuration.Trello.notifications))),
    Endpoint(Scoopit, Seq(
      Service("scoopit.wall", Configuration.Scoopit.wall))),
    Endpoint(BetaSeries, Seq(
      Service("betaseries.notifications", Configuration.BetaSeries.notifications),
      Service("betaseries.planning", Configuration.BetaSeries.planning),
      Service("betaseries.timeline", Configuration.BetaSeries.timeline))),
    Endpoint(RssProvider, Seq(
      Service("rss.rss", Configuration.Rss.rss)))
  )

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
  
        if (!config.manualNextResults) {
          // And "since" element to Url
          Some(sinceOpt.map(since => baseUrl + config.since.replace(":since", since)).getOrElse(baseUrl))
        } else {
          Some(baseUrl)
        }
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