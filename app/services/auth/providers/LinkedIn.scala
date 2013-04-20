package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import play.api.mvc.WithHeaders
import models.user.ProviderUser
import play.api.libs.concurrent.futureToPlayPromise
import models.user.SkimboToken

object LinkedIn extends OAuthProvider {

  override val name = "linkedin"
  override val namespace = "li"
  override val permissions = Seq(
    "r_fullprofile", // Get User details
    "r_emailaddress", // Get User email
    "rw_nus")         // Get Network activities
  override val permissionsSep = "+"

  // Override fetch method : define json format by default
  override def fetch(idUser: String, url: String) =
    super.fetch(idUser, url).withHeaders("x-li-format" -> "json")

  override def isInvalidToken(idUser:String, response: play.api.libs.ws.Response): Boolean = 
    response.status == 423
    
  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val fname = (me \ "firstName").asOpt[String]
      val lname = (me \ "lastName").asOpt[String]
      val displayName = Some(fname.getOrElse("") + " " + lname.getOrElse(""))
      val description = (me \ "headline").asOpt[String]
      val profileImage = (me \ "picture-url").asOpt[String]
      val token = getToken(idUser).get
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(token.token, Some(token.secret))), 
          displayName, 
          displayName, 
          description, 
          profileImage))
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details LINKEDIN")
        None
      }
    }
  }

}

