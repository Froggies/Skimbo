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
  override def fetch(url: String)(implicit request: RequestHeader) =
    super.fetch(url).withHeaders("x-li-format" -> "json")

  override def distantUserToSkimboUser(ident: String, response: play.api.libs.ws.Response)(implicit request: RequestHeader): Option[ProviderUser] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val fname = (me \ "firstName").asOpt[String]
      val lname = (me \ "lastName").asOpt[String]
      val displayName = Some(fname.getOrElse("") + " " + lname.getOrElse(""))
      val description = (me \ "headline").asOpt[String]
      val profileImage = (me \ "picture-url").asOpt[String]
      Some(ProviderUser(
          id, 
          this.name, 
          Some(SkimboToken(getToken.get.token, Some(getToken.get.secret))), 
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
  
  override def urlToPost(post:models.Post) = "http://api.linkedin.com/v1/people/~/shares"
  
  override def postContent(post:models.Post):String = {
    """<?xml version="1.0" encoding="UTF-8"?>
    <share>
      <comment>""" +post.title+ """</comment>
      <content>  
        <description>""" +post.message+ """</description> 
        <submitted-url>""" +post.url.getOrElse("")+ """</submitted-url> 
        <submitted-image-url>""" +post.url_image.getOrElse("")+ """</submitted-image-url> 
      </content>
      <visibility>
        <code>anyone</code>
      </visibility>
    </share>"""
  }

}

