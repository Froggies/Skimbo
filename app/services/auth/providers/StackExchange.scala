package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import services.auth.actions.WSGzipJson
import play.api.libs.json.JsValue

object StackExchange extends OAuth2Provider with WSGzipJson {

  override val name = "stackexchange"
  override val namespace = "se"
  override val method = Post
  override val permissions = Seq(
    "read_inbox", // Read inbox messages
    "no_expiry"   // Token do not expire
    )

  override def processToken(response: play.api.libs.ws.Response) = {
    val AuthQueryStringParser = """access_token=(.*)""".r
    response.body match {
      case AuthQueryStringParser(token) => Token(Some(token), None)
      case _                            => Token(None, None)
    }
  }

  // StackExchange need an additional auth key
  val KEY = config.getString("key").getOrElse("")

  // Override fetch method : add key to queries
  override def fetch(url: String)(implicit request: RequestHeader) =
    super.fetch(url).withQueryString("key" -> KEY)
    
  override def resultAsJson(response:play.api.libs.ws.Response):JsValue = parseGzipJson(response)
    
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = (resultAsJson(response) \ "items")(0)
      val id = (me \ "user_id").as[Int].toString
      val username = (me \ "display_name").asOpt[String]
      val name = (me \ "display_name").asOpt[String]
      val description = Some("Reputation" + (me \ "reputation").as[Int])
      val profileImage = (me \ "profile_image").asOpt[String]
      Some(ProviderUser(id, this.name, username, name, description, profileImage))
    } catch {
      case _ => {
        Logger.error("Error during fetching user details")
        None
      }
    }
  }
    
}

