package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object GooglePlus extends OAuth2Provider {

	override val name = "googleplus"
	override val namespace = "gp"
	override val method = Post
	override val permissionsSep = " "
	override val permissions = Seq(
		"https://www.googleapis.com/auth/userinfo.email", // View your email address
		"https://www.googleapis.com/auth/plus.me" // Know who you are on Google
	)

	override def processToken(response: play.api.libs.ws.Response) =
		Token((response.json \ "access_token").asOpt[String], (response.json \ "expires_in").asOpt[Int])

}

