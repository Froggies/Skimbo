package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import play.api.libs.ws.Response
import java.net.URLEncoder

object Facebook extends OAuth2Provider {

  override val name = "facebook"
  override val namespace = "fb"
  override val permissions: Seq[String] = Seq(
    "email",          // Get user email
    "read_stream",    // Get wall activity
    "read_mailbox",   // Get perso mails
    "publish_actions" // Post messages
  )
  override val additionalAccreditationParameters = Map(
    "display" -> "popup"
  )

  override def processToken(response: play.api.libs.ws.Response) = {
    val AuthQueryStringParser = """access_token=(.*)&expires=(.*)""".r
    response.body match {
      case AuthQueryStringParser(token, expires) => Token(Some(token), Some(expires.toInt))
      case _                                     => Token(None, None)
    }
  }
  
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val name = (me \ "name").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, None)), 
          name, 
          name, 
          None, 
          None))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details LINKEDIN")
        None
      }
    }
  }
  
  override def urlToPost(post:models.Post) = "https://graph.facebook.com/me/feed"
  
  override def postParams(post:models.Post):Seq[(String, String)] = {
    Seq("message" -> post.message) ++ 
    post.url.map(url => Seq("link" -> url)).getOrElse(Seq.empty) ++
    post.url_image.map(url => Seq("picture" -> url)).getOrElse(Seq.empty)
  }

}