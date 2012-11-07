package services.endpoints

import play.api.Logger
import play.api.mvc._
import services.endpoints.JsonRequest._
import play.api.libs.json.JsValue
import services.auth.GenericProvider

object Endpoints {

  // Activated endpoints
  val endpoints = Map[String, EndpointConfig](
      "twitter.wall" -> Configuration.Twitter.wall,
      "twitter.hashtag" -> Configuration.Twitter.hashtag,
      "twitter.user" -> Configuration.Twitter.user,
      "facebook.wall" -> Configuration.Facebook.wall
  )
  
  def getProvider(endpoint:String): Option[GenericProvider] = {
    endpoints.get(endpoint).map { config =>
      config.provider
    }.orElse(None)
  }

  def genererUrl(endpoint: String, param: Map[String, String], sinceOpt: Option[String]) : Option[String] = {
    endpoints.get(endpoint).map { config =>
      val isRequestValid = config.requiredParams.forall(param.get(_).isDefined) // Check if all required params are defined
      if (!isRequestValid) {
        Logger.error("Request invalid : all required params are not defined.")
        Logger.warn("Given : " + param)
        Logger.warn("Expected : " + config.requiredParams)
        None
      }
      
      val baseUrl = param.foldLeft(config.url)((url, param) => url.replace(":" + param._1, param._2)) // Generate url with params
      val completeUrl = sinceOpt.map(since => baseUrl.replace(":since", since)).getOrElse(baseUrl) // And "since" element to Url
      completeUrl
    }
  }
  
  def listEndpointsFromRequest(req: Request[AnyContent]) = {
    req.body.asJson.map(json => 
      listEndpointsFromJson(json)
    ).getOrElse(List.empty)
  }
  
  def listEndpointsFromJson(json: JsValue) = {
    (json \("channels")).as[List[UnifiedRequest]]
  }

}