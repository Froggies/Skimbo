package services.auth

import play.api._
import play.api.mvc._
import play.api.libs.oauth._
import play.api.Play.current
import play.api.libs.ws.WS

trait OAuthProvider extends GenericProvider {

	// Oauth1 configuration (from social.conf)
	lazy val config = current.configuration.getConfig("social."+name).get

	lazy val KEY = ConsumerKey(config.getString("clientId").get, config.getString("secret").get)

	lazy val service = OAuth(ServiceInfo(
		config.getString("requestToken").get+"?scope="+permissions.mkString(permissionsSep),
		config.getString("accessToken").get,
		config.getString("authorize").get, KEY),
		false)

	// Session and cookies fields
	lazy val fieldToken = namespace+"_token"
	lazy val fieldSecret = namespace+"_expires"

	/**
	 * Execute authentification on provider
	 * @param redirectRoute : Where the user wil be redirected after correct authentification
	 */
	override def auth(redirectRoute: Call)(implicit request: RequestHeader): Result = {
		request.getQueryString("oauth_verifier") match {

			// Step 1 : ask request token to provider and then redirect to accreditation page
			case None =>
				service.retrieveRequestToken(authRoute.absoluteURL(false)) match {
					case Right(t) => Redirect(service.redirectUrl(t.token)).withSession(request.session + (fieldToken -> t.token) + (fieldSecret -> t.secret))
					case Left(e) => throw e
				}

			// Step 2 : Retrieve access-token from WS and redirect to app
			case Some(verifier) =>
				service.retrieveAccessToken(getToken(request).get, verifier) match {
					case Right(t) => Redirect(redirectRoute).withSession(request.session + (fieldToken -> t.token) + (fieldSecret -> t.secret))
					case Left(e) => Redirect(redirectRoute).withSession(request.session + ("login-error" -> name))
				}
		}
	}

	override def getToken(implicit request: RequestHeader): Option[RequestToken] = {
		for {
			token <- request.session.get(fieldToken)
			secret <- request.session.get(fieldSecret)
		} yield {
			RequestToken(token, secret)
		}
	}

	override def fetch(url: String)(implicit request: RequestHeader) = {
		WS.url(url).sign(OAuthCalculator(KEY, getToken.get))
	}

}