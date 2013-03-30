package services.post

import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import services.auth.OAuthProvider
import models.Post
import play.api.mvc.RequestHeader
import play.api.libs.json.Json
import services.auth.AuthProvider
import models.ParamHelper
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait Poster {

  val authProvider:AuthProvider
  
  lazy val name: String = authProvider.name
  
  val canHavePageId: Boolean = false
  
  def hasToken(implicit request: RequestHeader):Boolean = authProvider.hasToken
  
  def urlToPost(post:Post):String = ""
  
  def postHeaderParams():Seq[(String, String)] = Seq.empty
    
  def postParams(post:Post)(implicit request: RequestHeader):Seq[(String, String)] = Seq.empty
  
  def postContent(post:Post):String = ""
  
  def post(post:Post)(implicit request: RequestHeader)
  
  def helperPageId(search: String)(implicit request: RequestHeader):Future[Seq[ParamHelper]] = Future(Seq.empty)
  
}

object Posters {
  
  val posters:Seq[Poster] = Seq(
      TwitterPoster, 
      FacebookPoster,
      FacebookPagePoster,
      LinkedinPoster,
      GithubPoster,
      GoogleplusPoster,
      ScoopitPoster)
      //services.auth.providers.Viadeo)      --> "Insufficient accreditation level" ! mais pas trouvé où le setter !!??
      //                                     --> recommended only for premium user
  
  def getPoster(name:String):Option[Poster] = {
    posters.find( _.name == name)
  }
  
  def toJson(implicit request: RequestHeader) = {
    val services = posters.map { service =>
      Json.obj(
        "service" -> service.name,
        "hasToken" -> service.hasToken,
        "canHavePageId" -> service.canHavePageId
      )
    }
    Json.toJson(services)
  }
  
}