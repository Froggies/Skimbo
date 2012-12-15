package services.auth.providers

import services.auth.GenericProvider
import play.api.libs.ws.WS
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import services.UserDao
import models.user.SkimboToken
import services.commands.Commands
import services.actors.UserInfosActor
import models.command.NewToken
import scala.concurrent.Await
import scala.concurrent.duration._
import models.user.ProviderUser
import play.api.Logger
import services.auth.GenericProvider
import services.auth.Token
import play.api.libs.concurrent.Execution.Implicits._

object BetaSeries extends GenericProvider {

  override val name = "betaseries"
  override val namespace = "bs"
  lazy val secret = config.getString("secret").get
    
  override def getToken(implicit request: RequestHeader) = {
    request.session.get("id").flatMap( id => Await.result(UserDao.getToken(id, this), 1 second))
  }
  
  override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    println(request.uri)
    request.getQueryString("token") match {
      case None => {
        Async {
          val req = WS.url(config.getString("accessToken").get+"?key="+secret)
          req.get.map { response =>
            println(response.json)
            val code = (response.json \ "root" \ "oauth" \ "key").as[String]
            Redirect(config.getString("authorize").get+"?key="+code)
          }
        }
      }
      case Some(token) => {
        val session = generateUniqueId(request.session)
        //TODO rework this, same lignes in OAuthProvider and OAuthProvider2 and BetaSeries
        UserDao.setToken(session("id"), this, SkimboToken(token))
        Commands.interpretCmd(session("id"), NewToken.asCommand(this))
        UserInfosActor.refreshInfosUser(session("id"), this)
        UserInfosActor.restartProviderColumns(session("id"), this)
        Redirect(redirectRoute).withSession(session)
      }
    }
  }
    
  override def fetch(url: String)(implicit request: RequestHeader) = {
    WS.url(url).withQueryString("key" -> secret, "token" -> getToken.get.token)
  }
  
  override def distantUserToSkimboUser(id: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = (response.json \ "root" \ "member")
      val username = (me \ "login").as[String]
      val name = (me \ "login").as[String]
      val description = Some("")//TODO put some stats !?
      val profileImage = (me \ "avatar").asOpt[String]
      Some(ProviderUser(
          username, 
          this.name, 
          Some(SkimboToken(getToken.get.token)),
          Some(username), 
          Some(name), 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details BetaSeries")
        None
      }
    }
  }
  
}