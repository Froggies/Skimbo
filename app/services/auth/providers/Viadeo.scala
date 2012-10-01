package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object Viadeo extends OAuth2Provider {

	override val name = "viadeo"
	override val namespace = "vi"
	override val method = Post

	override def processToken(response: play.api.libs.ws.Response) =
		Token((response.json \ "access_token").asOpt[String], None)

}