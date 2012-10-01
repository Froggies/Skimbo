package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object StackExchange extends OAuth2Provider {

	override val name = "stackexchange"
	override val namespace = "se"
	override val method = Post
	override val permissions = Seq(
		"read_inbox", // Read inbox messages
		"no_expiry" 	// Token do not expire
	)

	override def processToken(response: play.api.libs.ws.Response) = {
		val AuthQueryStringParser = """access_token=(.*)""".r
		response.body match {
			case AuthQueryStringParser(token) => Token(Some(token), None)
			case _ => Token(None, None)
		}
	}

	// StackExchange need an additional auth key
	val KEY = config.getString("key").getOrElse("")
	
	// Override fetch method : add key to queries
	override def fetch(url: String)(implicit request: RequestHeader) =
		super.fetch(url).withQueryString("key" -> KEY)

}

