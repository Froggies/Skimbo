package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.User

object Trello extends OAuthProvider {

  override val name = "trello"
  override val namespace = "tr"

  override def distantUserToSkimboUser(response: play.api.libs.ws.Response): Option[User] = {
    try {
      val me = response.json
      val id = (me \ "id").as[String]
      val username = (me \ "username").as[String]
      val name = (me \ "fullName").as[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "gravatarHash").asOpt[String]
      if(profileImage.isDefined)
        Some(User(id, username, name, this.name, description, Some("http://www.gravatar.com/avatar/"+profileImage.get)))
      else
        Some(User(id, username, name, this.name, description, profileImage))
    } catch {
      case _ => {
        Logger.error("Error during fetching user details")
        None
      }
    }
  }
    
  /*{
    "id":"501cf5007d3f7888395dbf71",
    "avatarHash":"",
    "bio":"",
    "fullName":"r-m@n",
    "initials":"M@N",
    "status":"active",
    "url":"https://trello.com/rmaneschi",
    "username":"rmaneschi",
    "avatarSource":"none",
    "confirmed":true,
    "email":null,
    "gravatarHash":"c6b552a4cd47f7cf1701ea5b650cd2e3",
    "idBoards":["501cf5007d3f7888395dbf85","501c1b7a46306f016a1b5868","505a376f783db194689c3972"],
    "idBoardsInvited":[],
    "idBoardsPinned":["501cf5007d3f7888395dbf85","501c1b7a46306f016a1b5868","505a376f783db194689c3972"],
    "idOrganizations":[],
    "idOrganizationsInvited":[],
    "idPremOrgsAdmin":[],
    "loginTypes":null,
    "prefs":{
      "sendSummaries":true,
      "minutesBetweenSummaries":60,
      "minutesBeforeDeadlineToNotify":1440,
      "colorBlind":false},
    "trophies":[],
    "uploadedAvatarHash":null
  }*/
    
}

