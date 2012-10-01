package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object Facebook extends OAuth2Provider {

	override val name = "facebook"
	override val namespace = "fb"
	override val permissions: Seq[String] = Seq(
		"email", 			// Get user email
		"read_stream" // Get wall activity
	)

	override def processToken(response: play.api.libs.ws.Response) = {
		val AuthQueryStringParser = """access_token=(.*)&expires=(.*)""".r
		response.body match {
			case AuthQueryStringParser(token, expires) => Token(Some(token), Some(expires.toInt))
			case _ => Token(None, None)
		}
	}

}