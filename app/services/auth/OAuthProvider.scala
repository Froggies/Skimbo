package services.auth

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import models.command.NewToken
import models.user.SkimboToken
import play.api.libs.oauth._
import play.api.libs.ws.WS
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import services.UserDao
import services.post.Poster
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait OAuthProvider extends AuthProvider with Poster {

  lazy val KEY = ConsumerKey(config.getString("clientId").get, config.getString("secret").get)

  lazy val service = OAuth(ServiceInfo(
    config.getString("requestToken").get + "?scope=" + permissions.mkString(permissionsSep),
    config.getString("accessToken").get,
    config.getString("authorize").get, KEY),
    false)

  // Session and cookies fields
  lazy val fieldToken = namespace + "_token"
  lazy val fieldSecret = namespace + "_expires"

  /**
   * Execute authentication on provider
   * @param redirectRoute : Where the user will be redirected after correct authentication
   */
  override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    request.getQueryString("oauth_verifier") match {

      // Step 1 : ask request token to provider and then redirect to accreditation page
      case None =>
        service.retrieveRequestToken(authRoute.absoluteURL(false)) match {
          case Right(t) => Redirect(service.redirectUrl(t.token))
            .withSession(request.session + (fieldToken -> t.token) + (fieldSecret -> t.secret))
          case Left(e) => throw e
        }

      // Step 2 : Retrieve access-token from WS and redirect to app
      case Some(verifier) =>
        service.retrieveAccessToken(getSessionToken, verifier) match {
          case Right(t) => {
            startUser(SkimboToken(t.token, Some(t.secret)), redirectRoute)
          }
          case Left(e) => Redirect(redirectRoute).flashing("login-error" -> name)
        }
    }
  }

  private def getSessionToken(implicit request: RequestHeader) = {
    RequestToken(request.session.get(fieldToken).get, request.session.get(fieldSecret).get)
  }

  override def getToken(implicit request: RequestHeader): Option[RequestToken] = {
    val skimboToken = request.session.get("id").flatMap(id => Await.result(UserDao.getToken(id, this), 1 second))
    skimboToken.map(st => RequestToken(st.token, st.secret.get))
  }

  override def fetch(url: String)(implicit request: RequestHeader) = {
    WS.url(url).sign(OAuthCalculator(KEY, getToken.get))
  }
  
  override def post(post:models.Post)(implicit request: RequestHeader) = {
    println("POST "+name+" = "+post)
    WS.url(urlToPost(post))
      .withQueryString(postParams(post):_*)
      .sign(OAuthCalculator(KEY, getToken.get))
      .post(postContent(post))
      .onComplete {
        case Success(response) => {
          println(response.body.toString)
        }
        case Failure(error) => {
          println(error)
        }
    }
  }
  
}