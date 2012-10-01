package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object GitHub extends OAuth2Provider {

	override val name = "github"
	override val namespace = "gh"
	override val method = Post
	override val accessTokenHeaders = Seq("Accept" -> "application/json")
	
	override def processToken(response: play.api.libs.ws.Response) = 
		Token((response.json \ "access_token").asOpt[String], None)

}