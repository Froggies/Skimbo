package services.auth.providers

import play.api._
import play.api.mvc._
import models.user.ProviderUser
import services.auth._
import play.api.libs.ws.Response
import models.user.SkimboToken
import java.net.URLEncoder
import services.post.Starer
import parser.json.providers.TwitterUser
import services.commands.CmdToUser

object Twitter extends OAuthProvider {

  override val name = "twitter"
  override val namespace = "tw"

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    if(isInvalidToken(ident, response)) {
      CmdToUser.sendTo(ident, models.command.TokenInvalid(name))
      None
    } else {
      try {
        TwitterUser.asProviderUser(response.json)
      } catch {
        case t:Throwable => {
          Logger.error("Error during fetching user details TWITTER (certainly Rate Limit Exceeded)")
          Logger.error(response.json.toString)
          None
        }
      }
    }
  }
  
  override def isInvalidToken(idUser:String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Boolean = 
    response.status != 200 && response.status != 429 && response.status != 404
  
  override def isRateLimiteError(response: Response): Boolean = response.status == 429
  
  override def urlToPost(post:models.Post) = 
    "https://api.twitter.com/1.1/statuses/update.json?status="+URLEncoder.encode(post.message, "UTF-8")
  
  override def urlToStar(idProvider:String) = "https://api.twitter.com/1.1/statuses/retweet/"+idProvider+".json"
  
}

