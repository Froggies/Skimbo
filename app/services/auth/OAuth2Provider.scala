package services.auth

import java.util.UUID.randomUUID
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import models.command.NewToken
import models.user.SkimboToken
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WS
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import services.dao.UserDao
import services.post.Poster
import scala.util.Success
import scala.util.Failure
import services.post.Starer

trait OAuth2Provider extends AuthProvider with Starer {

  // OAuth2 provider settings (override these)
  def method: Verb = Get
  def accessTokenHeaders: Seq[(String, String)] = Seq.empty

  // Define how to get token and expires timestamp from WS response
  def processToken(response: play.api.libs.ws.Response): Token
  def additionalAccreditationParameters: Map[String, String] = Map.empty

  // Session and cookies fields
  lazy val fieldCsrf = namespace + "_csrf"
  lazy val fieldToken = namespace + "_token"
  lazy val fieldExpires = namespace + "_expires"

  // Load infos (token, secret, urls) from configuration
  lazy val clientId = config.getString("clientId").get
  lazy val secret = config.getString("secret").get
  lazy val authorizeUrl = config.getString("authorize").get
  lazy val accessTokenUrl = config.getString("accessToken").get

  /**
   * Execute authentication on provider
   * @param redirectRoute : Where the user wil be redirected after correct authentication
   */
  override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    request.getQueryString("code") match {
      case None => redirectToAccreditationPage // Step one : redirect the user to the service's accreditation page
      case Some(code) => {
        retrieveAccessToken(code, redirectRoute) // Step two : Retrieve authentication token from WS
      }
    }
  }

  /**
   * Shortcut to make WS request without passing token as parameter
   * TOKEN must be in session
   */
  override def fetch(url: String)(implicit request: RequestHeader) = {
    WS.url(url).withQueryString("access_token" -> getToken.get.token)
  }

  /**
   * Get token from session if exists
   */
  override def getToken(implicit request: RequestHeader) = {
    request.session.get("id").flatMap(id => Await.result(UserDao.getToken(id, this), 1 second))
  }

  /**
   * Redirect the user to the service's accreditation page
   */
  private def redirectToAccreditationPage(implicit request: RequestHeader) = {
    val csrf = randomUUID().toString
    val redirectQueryString = Map(
      "client_id" -> clientId,
      "redirect_uri" -> authRoute.absoluteURL(false),
      "state" -> csrf,
      "scope" -> permissions.mkString(permissionsSep),
      "response_type" -> "code")

    val url = (redirectQueryString ++ additionalAccreditationParameters)
      .foldLeft(authorizeUrl + "?")((url, qs) => url + qs._1 + "=" + qs._2 + "&")
    
    Logger("OAuth2provider").info("URL => "+url)
    Redirect(url).withSession(request.session + (fieldCsrf -> csrf))
  }

  /**
   * Check authentication code and fetch accessToken from provider WS
   */
  private def retrieveAccessToken(code: String, redirectRoute: Call)(implicit request: RequestHeader) = {

    // Check if CSRF fields are ok
    val sess_csrf = request.session.get(fieldCsrf)
    val verif_csrf = request.getQueryString("state")

    if (sess_csrf.isDefined && sess_csrf == verif_csrf) {

      // Send request to retrieve auth token and parse response
      Async {
        fetchAccessTokenResponse(code).map(response =>
          processTokenSafe(response) match {

            // Provider return token
            case Token(Some(token), _, refresh) => {
              startUser(SkimboToken(token, refresh), redirectRoute)
            }

            // Provider return nothing > an error has occurred during authentication
            case _ =>
              Redirect(redirectRoute).flashing("login-error" -> name)
          })
      }

      // CRSF attack detected
    } else {
      Logger.warn("CSRF attack detected from IP %s" format (request.remoteAddress))
      Redirect(redirectRoute).flashing("login-error" -> "security")
    }

  }

  /**
   * Call provider WS to fetch token string (json or queryString)
   */
  protected def fetchAccessTokenResponse(code: String)(implicit request: RequestHeader) = {
    val data = Map(
      "client_id" -> clientId,
      "client_secret" -> secret,
      "code" -> code,
      "redirect_uri" -> authRoute.absoluteURL(false),
      "grant_type" -> "authorization_code")

    val req = WS.url(accessTokenUrl).withHeaders(accessTokenHeaders: _*)

    method match {
      case Get => req.withQueryString(data.toSeq: _*).get
      case Post => req.post(data.mapValues(Seq(_)))
    }
  }

  protected def processTokenSafe(response: play.api.libs.ws.Response): Token = {
    try {
      processToken(response)
    } catch {
      case _: Throwable => {
        Logger.error("[" + name + "] Unable to process token")
        Logger.info(response.body)
        Token(None, None)
      }
    }
  }
  
  override def post(url:String, queryString:Seq[(String, String)], headers:Seq[(String, String)], content:String)(implicit request: RequestHeader) = {
    val queryS = queryString ++ Seq("access_token" -> getToken.get.token)
    WS.url(url)
      .withQueryString( queryS:_* )
      .withHeaders(headers:_*)
      .post(content)
  }
  
  override def star(idProvider: String)(implicit request: RequestHeader) = {
    val queryString = Seq("access_token" -> getToken.get.token)
    WS.url(urlToStar(idProvider))
      .withQueryString( queryString:_* )
      .post("")
  }

}

/**
 * Token send from provider
 */
sealed case class Token(token: Option[String], expires: Option[Int], refreshToken: Option[String] = None)

sealed abstract class Verb
case object Get extends Verb
case object Post extends Verb
