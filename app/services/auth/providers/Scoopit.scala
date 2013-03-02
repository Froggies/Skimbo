package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import java.net.URLEncoder

object Scoopit extends OAuthProvider {

  override val name = "scoopit"
  override val namespace = "sc"

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json \ "user"
      val id = (me \ "id").as[Int].toString
      val username = (me \ "shortName").asOpt[String]
      val name = (me \ "name").asOpt[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "avatarUrl").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, Some(getToken.get.secret))), 
          username, 
          name, 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details SCOOPIT")
        None
      }
    }
  }
  
  override def urlToPost(post:models.Post) = "http://www.scoop.it/api/1/post"
  
  override def postParams(post:models.Post):Seq[(String, String)] = {
    Seq(
      "action" -> "create",
      "title" -> URLEncoder.encode(post.title, "UTF-8"),
      "url" -> URLEncoder.encode(post.url.getOrElse(""), "UTF-8"),
      "imageUrl" -> URLEncoder.encode(post.url_image.getOrElse(""), "UTF-8"),
      "content" -> URLEncoder.encode(post.message, "UTF-8")
    )
  }

}