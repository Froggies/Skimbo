package services.auth

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import models.user.SkimboToken
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.OAuth
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.ServiceInfo
import play.api.libs.ws.WS
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import services.dao.UserDao

trait OAuthProvider extends AuthProvider {

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
            Async {
              startUser(SkimboToken(t.token, Some(t.secret)), redirectRoute)
            }
          }
          case Left(e) => Redirect(redirectRoute).flashing("login-error" -> name)
        }
    }
  }

  private def getSessionToken(implicit request: RequestHeader) = {
    RequestToken(request.session.get(fieldToken).get, request.session.get(fieldSecret).get)
  }

  override def getToken(idUser: String): Option[RequestToken] = {
    val skimboToken = Await.result(UserDao.getToken(idUser, this), 10 second)
    skimboToken.map(st => RequestToken(st.token, st.secret.get))
  }

  override def fetch(idUser: String, url: String) = {
    WS.url(url).sign(OAuthCalculator(KEY, getToken(idUser).get))
  }

  override def post(idUser: String, url: String, queryString: Seq[(String, String)], headers: Seq[(String, String)], content: String) = {
    WS.url(url)
      .withQueryString(queryString: _*)
      .withHeaders(headers: _*)
      .sign(OAuthCalculator(KEY, getToken(idUser).get))
      .post(content)
  }
  
  override def post(idUser: String, url: String, queryString: Seq[(String, String)], headers: Seq[(String, String)], content: Map[String, Seq[String]]) = {
    WS.url(url)
      .withQueryString(queryString: _*)
      .withHeaders(headers: _*)
      .sign(OAuthCalculator(KEY, getToken(idUser).get))
      .post(content)
  }

}