package services.auth.providers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import parser.json.PathDefaultReads
import play.api.libs.functional.syntax._
import java.net.URLEncoder

object GooglePlus extends OAuth2Provider {

  override val name = "googleplus"
  override val namespace = "gp"
  override val method = Post
  override val permissionsSep = " "
  override val permissions = Seq(
    "https://www.googleapis.com/auth/userinfo.email", // View your email address
    "https://www.googleapis.com/auth/plus.login") // Know who you are on Google

  override def additionalAccreditationParameters = Map("request_visible_actions" -> "http://schemas.google.com/AddActivity")
    
  override def processToken(response: play.api.libs.ws.Response) = 
    Token((response.json \ "access_token").asOpt[String], (response.json \ "expires_in").asOpt[Int])
  
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json // TODO : En faire un parser
      Logger("G+Provider").info(me.toString)
      val id = (me \ "id").as[String]
      val username = (me \ "displayName").asOpt[String]
      val name = (me \ "name" \ "familyName").asOpt[String]
      val description = Some("")
      val profileImage = (me \ "image" \ "url").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, None)), 
          username, 
          name, 
          description, 
          profileImage))
    } catch {
      case t:Throwable => {
        Logger("G+Provider").error("Error during fetching user details G+ "+t.getMessage())
        None
      }
    }
  }
  
  override def isInvalidToken(response: play.api.libs.ws.Response): Boolean = {
    (response.json \ "error" \ "code").asOpt[Int].getOrElse(0) == 401
  }
  
  override def urlToPost(post:models.Post) = "https://www.googleapis.com/plus/v1/people/me/moments/vault"
  
  override def postHeaderParams = Seq(("Content-Type", "application/json; charset=UTF-8"))
    
  override def postContent(post:models.Post):String = {
    val res = Json.obj(
      "type" -> "http://schemas.google.com/AddActivity",
      "target" -> Json.obj(
        "name" -> post.title,
        "id" -> post.title,
        "description" -> post.message,
        "type" -> "http://schemas.google.com/AddActivity",
        "image" -> post.url_image
        //"url" -> post.url
      )
    ).toString
    Logger("GooglePlusParser").info(res)
    res
  }

}

