package services.auth

import play.api._
import play.api.mvc._
import play.api.libs.oauth._
import play.api.libs.ws._
import play.api.libs.concurrent.execution.defaultContext
import play.api.Play.current
import play.api.Logger
import java.net.URLEncoder.encode
import java.util.UUID.randomUUID

trait OAuth2Provider extends GenericProvider {

  // OAuth2 provider settings (override these)
  def method: Verb = Get
  def accessTokenHeaders: Seq[(String, String)] = Seq.empty

  // Define how to get token and expires timestamp from WS response
  def processToken(response: play.api.libs.ws.Response): Token

  // Session and cookies fields
  lazy val fieldCsrf 			= namespace+"_csrf"
  lazy val fieldToken 		= namespace+"_token"
  lazy val fieldExpires 	= namespace+"_expires"

  // Load infos (token, secret, urls) from configuration
  lazy val config 				= current.configuration.getConfig("social."+name).get
  lazy val clientId 			= config.getString("clientId").get
  lazy val secret 				= config.getString("secret").get
  lazy val authorizeUrl 	= config.getString("authorize").get
  lazy val accessTokenUrl = config.getString("accessToken").get

  /**
   * Execute authentification on provider
   * @param redirectRoute : Where the user wil be redirected after correct authentification
   */
  override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
    request.getQueryString("code") match {
      case None       => redirectToAccreditationPage // Step one : redirect the user to the service's accreditation page
      case Some(code) => retrieveAccessToken(code, redirectRoute) // Step two : Retrieve authentication token from WS
    }
  }

  /**
   * Shortcut to make WS request without passing token as parameter
   * TOKEN must be in session
   */
  override def fetch(url: String)(implicit request: RequestHeader) = {
    WS.url(url).withQueryString("access_token" -> getToken.get)
  }

  /**
   * Get token from session if exists
   */
  override def getToken(implicit request: RequestHeader) = request.session.get(fieldToken)

  /**
   * Redirect the user to the service's accreditation page
   */
  private def redirectToAccreditationPage(implicit request: RequestHeader) = {
    val csrf = randomUUID().toString
    val redirectQueryString = Map(
      "client_id" 		-> clientId,
      "redirect_uri" 	-> authRoute.absoluteURL(false),
      "state" 				-> csrf,
      "scope" 				-> permissions.mkString(permissionsSep),
      "response_type" -> "code")

    val url = redirectQueryString.foldLeft(authorizeUrl+"?")((url, qs) => url + qs._1+"="+qs._2+"&")

    Redirect(url).withSession(request.session + (fieldCsrf -> csrf))
  }

  /**
   * Check authentification code and fetch accessToken from provider WS
   */
  private def retrieveAccessToken(code: String, redirectRoute: Call)(implicit request: RequestHeader) = {

    // Check if CSRF fields are ok
    val sess_csrf = request.session.get(fieldCsrf)
    val verif_csrf = request.getQueryString("state")

    if (sess_csrf.isDefined && sess_csrf == verif_csrf) {

      // Send request to retrieve auth token and parse response
      Async {
        fetchAccessTokenResponse(code).map(response =>
          processToken(response) match {

            // Provider return both token and expires timestamp
            case Token(Some(token), Some(expires)) =>
              Redirect(redirectRoute).withSession(request.session + (fieldToken -> token) + (fieldExpires -> expires.toString))

            // Provider return only token
            case Token(Some(token), None) =>
              Redirect(redirectRoute).withSession(request.session + (fieldToken -> token))

            // Provider return nothing > an error has occurred during authentication
            case _ =>
              Redirect(redirectRoute).withSession(request.session - fieldToken).flashing("login-error" -> name)
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
  private def fetchAccessTokenResponse(code: String)(implicit request: RequestHeader) = {
    val data = Map(
      "client_id" 		-> clientId,
      "client_secret" -> secret,
      "code" 					-> code,
      "redirect_uri" 	-> authRoute.absoluteURL(false),
      "grant_type" 		-> "authorization_code")

    val req = WS.url(accessTokenUrl).withHeaders(accessTokenHeaders: _*)

    method match {
      case Get  => req.withQueryString(data.toSeq: _*).get
      case Post => req.post(data.mapValues(Seq(_)))
    }
  }

}

/**
 * Token send from prider
 */
sealed case class Token(token: Option[String], expires: Option[Int])

// Method to retrieve token
sealed abstract class Verb
case object Get extends Verb
case object Post extends Verb
