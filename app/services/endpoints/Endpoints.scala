package services.endpoints

import play.api.Logger
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.api.mvc.Request
import services.auth.GenericProvider
import services.auth.RssProvider
import services.auth.providers._
import services.endpoints.JsonRequest.UnifiedRequest
import services.endpoints.JsonRequest.unifiedRequestReader

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
      Configuration.Facebook.notification,
      Configuration.Facebook.user,
      Configuration.Facebook.group,
      Configuration.Facebook.page,
      Configuration.Facebook.message)),
    Endpoint(Viadeo, Seq(
      Configuration.Viadeo.smartNews,
      Configuration.Viadeo.newsfeed,
      Configuration.Viadeo.homeNewsfeed,
      Configuration.Viadeo.inbox)),
    Endpoint(LinkedIn, Seq(
      Configuration.Linkedin.wall)),
    Endpoint(GooglePlus, Seq(
      Configuration.GooglePlus.wall,
      Configuration.GooglePlus.user)),
    Endpoint(GitHub, Seq(
      Configuration.Github.userEvents)),
    Endpoint(Bitbucket, Seq(
      Configuration.Bitbucket.eventsRepo,
      Configuration.Bitbucket.commits)),
    Endpoint(Trello, Seq(
      Configuration.Trello.notifications)),
    Endpoint(Scoopit, Seq(
      Configuration.Scoopit.wall,
      Configuration.Scoopit.topic)),
    Endpoint(BetaSeries, Seq(
      Configuration.BetaSeries.planning,
      Configuration.BetaSeries.timeline)),
    Endpoint(RssProvider, Seq(
      Configuration.Rss.rss)),
    Endpoint(Reddit, Seq(
      Configuration.Reddit.hot,
      Configuration.Reddit.top,
      Configuration.Reddit.newer,
      Configuration.Reddit.subreddit))
  )

  def getAll: Seq[Endpoint] = {
    endpoints
  }
  
  def getGenericProvider(provider: String): Option[GenericProvider] = {
    val found = endpoints.filter { _.provider.name == provider }
    found.headOption.map(_.provider)
  }
  
  def getProvider(service: String): Option[GenericProvider] = {
    val found = endpoints.filter { _.services.filter { _.uniqueName == service }.size > 0 }
    found.headOption.map(_.provider)
  }

  def getConfig(service: String): Option[EndpointConfig] = {
    val found = endpoints.flatMap {
      _.services.filter { endServ =>
        endServ.uniqueName == service
      }
    }
    found.headOption
  }

  def genererUrl(endpoint: String, params: Map[String, String], sinceOpt: Option[String]): Option[String] = {
    getConfig(endpoint).flatMap { config =>
      val isRequestValid = config.requiredParams.forall(params.get(_).isDefined) // Check if all required params are defined
      if (!isRequestValid) {
        Logger.error("Request invalid : all required params are not defined.")
        Logger.warn("Given : " + params)
        Logger.warn("Expected : " + config.requiredParams)
        None
      } else {
        // Transform params with configured filters
        val tidyedParams = params.map {
          case (key, value) => (key -> config.transformParams.get(key).map(tidyer => tidyer(value)).getOrElse(value))
        }
        // Generate url with params
        val baseUrl = tidyedParams.foldLeft(config.url)((url, param) => url.replace(":" + param._1, param._2))
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
