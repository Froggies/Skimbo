package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import play.api.mvc.WithHeaders

object LinkedIn extends OAuthProvider {

	override val name = "linkedin"
	override val namespace = "li"
	override val permissions = Seq(
			"r_basicprofile",		// Get User details
			"r_emailaddress",		// Get User email
			"rw_nus")						// Get Network activities
	override val permissionsSep = "+"
		
	def authenticate = Action { implicit request =>
		auth(controllers.routes.SocialNetworksTest.linkedin)
	}
	
	// Override fetch method : define json format by default
	override def fetch(url: String)(implicit request: RequestHeader) = 
		super.fetch(url).withHeaders("x-li-format" -> "json")

}

