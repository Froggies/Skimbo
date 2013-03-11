package services.post

import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import services.auth.OAuthProvider
import models.Post
import play.api.mvc.RequestHeader
import play.api.libs.json.Json

trait Poster {

  def name: String
  
  def hasToken(implicit request: RequestHeader):Boolean
  
  def urlToPost(post:Post):String = ""
  
  def postHeaderParams():Seq[(String, String)] = Seq.empty
    
  def postParams(post:Post):Seq[(String, String)] = Seq.empty
  
  def postContent(post:Post):String = ""
  
  def post(post:Post)(implicit request: RequestHeader)
  
}

object Posters {
  
  val posters:Seq[Poster] = Seq(
      services.auth.providers.Twitter, 
      services.auth.providers.Facebook,
      services.auth.providers.LinkedIn,
      services.auth.providers.GitHub,
      services.auth.providers.GooglePlus)
      //services.auth.providers.Viadeo)      --> "Insufficient accreditation level" ! mais pas trouvé où le setter !!??
      //                                     --> recommended only for premium user
      //TODO services.auth.providers.Scoopit --> missing topicId !!??
  
  def getPoster(name:String):Option[Poster] = {
    posters.find( _.name == name)
  }
  
  def toJson(implicit request: RequestHeader) = {
    val services = posters.map { service =>
      Json.obj(
        "name" -> service.name,
        "hasToken" -> service.hasToken
      )
    }
    Json.toJson(services)
  }
  
}