package services.auth.providers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import models.command.NewToken
import models.user.ProviderUser
import models.user.SkimboToken
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WS
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import services.dao.UserDao
import services.auth.AuthProvider

object BetaSeries extends AuthProvider {

  override val name = "betaseries"
  override val namespace = "bs"
  lazy val secret = config.getString("secret").get

  override def getToken(idUser: String) = {
    Await.result(UserDao.getToken(idUser, this), 1 second)
  }

  override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    println(request.uri)
    request.getQueryString("token") match {
      case None => {
        Async {
          val req = WS.url(config.getString("accessToken").get + "?key=" + secret)
          req.get.map { response =>
            println(response.json)
            val code = (response.json \ "root" \ "oauth" \ "key").as[String]
            Redirect(config.getString("authorize").get + "?key=" + code)
          }
        }
      }
      case Some(token) => {
        Async {
          startUser(SkimboToken(token), redirectRoute)
        }
      }
    }
  }

  override def fetch(idUser: String, url: String) = {
    WS.url(url).withQueryString("key" -> secret, "token" -> getToken(idUser).get.token)
  }
  
  //TODO not tested because nothing to post :D
  override def post(idUser: String, url:String, queryString:Seq[(String, String)], headers:Seq[(String, String)], content:String) = {
   val queryS = queryString ++ Seq("key" -> secret, "token" -> getToken(idUser).get.token)
    WS.url(url)
      .withQueryString( queryS:_* )
      .withHeaders(headers:_*)
      .post(content)
  }

  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = (response.json \ "root" \ "member")
      val username = (me \ "login").as[String]
      val name = (me \ "login").as[String]
      val description = Some("") //TODO put some stats !?
      val profileImage = (me \ "avatar").asOpt[String]
      Some(ProviderUser(
        username,
        this.name,
        Some(SkimboToken(getToken(idUser).get.token)),
        Some(username),
        Some(name),
        description,
        profileImage))
    } catch {
      case _: Throwable => {
        Logger.error("Error during fetching user details BetaSeries")
        None
      }
    }
  }

}