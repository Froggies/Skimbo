package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import play.api.libs.ws.Response
import java.net.URLEncoder
import services.commands.CmdToUser
import parser.json.providers.FacebookUser
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.json.JsValue

object Facebook extends OAuth2Provider {

  override val name = "facebook"
  override val namespace = "fb"
  override val permissions: Seq[String] = Seq(
    "email",          // Get user email
    "read_stream",    // Get wall activity
    "read_mailbox",   // Get directs messages
    "publish_actions",// Post messages
    "manage_pages",   // Post on pages
    "publish_stream"  // Post on pages
  )
  
  override val additionalAccreditationParameters = Map(
    "display" -> "popup",
    "auth_type" -> "reauthenticate"
  )

  override def processToken(response: play.api.libs.ws.Response) = {
    val AuthQueryStringParser2 = """access_token=(.*)&expires=(.*)&auth_type=reauthenticate""".r
    val AuthQueryStringParser3 = """access_token=(.*)&auth_type=reauthenticate""".r
    response.body match {
      //case AuthQueryStringParser(token, expires) => Token(Some(token), Some(expires.toInt), None)
      case AuthQueryStringParser2(token, expires)=> Token(Some(token), Some(expires.toInt), None)
      case AuthQueryStringParser3(token)         => Token(Some(token), None, None)
      case _                                     => Token(None, None)
    }
  }
  
  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    if(isInvalidToken(idUser, response)) {
      CmdToUser.sendTo(idUser, models.command.TokenInvalid(name))
      None
    } else {
      try {
        FacebookUser.asProviderUser(idUser, response.json)
      } catch {
        case t:Throwable => {
          Logger.error("Error during fetching user details FACEBOOK")
          Logger.error(response.json.toString)
          None
        }
      }
    }
  }
  
}