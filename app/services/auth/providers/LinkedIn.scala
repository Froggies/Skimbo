package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import play.api.mvc.WithHeaders
import models.user.ProviderUser
import play.api.libs.concurrent.futureToPlayPromise

object LinkedIn extends OAuthProvider {

  override val name = "linkedin"
  override val namespace = "li"
  override val permissions = Seq(
    "r_fullprofile", // Get User details
    "r_emailaddress", // Get User email
    "rw_nus")         // Get Network activities
  override val permissionsSep = "+"

  // Override fetch method : define json format by default
  override def fetch(url: String)(implicit request: RequestHeader) =
    super.fetch(url).withHeaders("x-li-format" -> "json")

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val username = (me \ "firstname").asOpt[String]
      val name = (me \ "lastname").asOpt[String]
      val description = (me \ "headline").asOpt[String]
      val profileImage = (me \ "picture-url").asOpt[String]
      Some(ProviderUser(id, this.name, username, name, description, profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details LINKEDIN")
        None
      }
    }
  }

}

