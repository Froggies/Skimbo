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
import parser.json.providers.GoogleplusUser
import services.commands.CmdToUser
import play.api.libs.ws.WS
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import services.dao.UserDao
import services.commands.CmdFromUser
import services.actors.UserInfosActor
import models.command.NewToken

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
    Token((response.json \ "access_token").asOpt[String], 
        (response.json \ "expires_in").asOpt[Int],
        (response.json \ "refresh_token").asOpt[String])
  
  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    if(isInvalidToken(ident, response)) {
      CmdToUser.sendTo(ident, models.command.TokenInvalid(name))
      None
    } else {
      try {
        GoogleplusUser.asProviderUser(response.json)
      } catch {
        case t:Throwable => {
          Logger("G+Provider").error("Error during fetching user details G+ "+t.getMessage())
          Logger("G+Provider").error(response.json.toString)
          None
        }
      }
    }
  }
  
  override def isInvalidToken(idUser:String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Boolean = {
    if(response.status == 401) {
      val data = Map(
        "client_id" -> clientId,
        "client_secret" -> secret,
        "grant_type" -> "authorization_code",
        "refresh_token" -> getToken.get.secret.get)
  
      val req = WS.url(accessTokenUrl).withHeaders(accessTokenHeaders: _*)
  
      val res = Await.result(req.post(data.mapValues(Seq(_))), Duration("5 seconds"))
      processTokenSafe(response) match {
        // Provider return token
        case Token(Some(token), _, refresh) => {
          UserDao.setToken(idUser, this, SkimboToken(token, refresh))
          CmdFromUser.interpretCmd(idUser, NewToken.asCommand(this))
          UserInfosActor.refreshInfosUser(idUser, this)
          UserInfosActor.restartProviderColumns(idUser, this)
          false
        }
        // Provider return nothing > an error has occurred during authentication
        case _ =>
          true
      }
    } else {
      false
    }
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

