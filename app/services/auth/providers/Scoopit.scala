package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.User

object Scoopit extends OAuthProvider {

  override val name = "scoopit"
  override val namespace = "sc"
    
  override def distantUserToSkimboUser(response: play.api.libs.ws.Response): Option[User] = {
    try {
      val me = response.json \ "user"
      val id = (me \ "id").as[Int]
      val username = (me \ "shortName").as[String]
      val name = (me \ "name").as[String]
      val description = (me \ "bio").asOpt[String]
      val profileImage = (me \ "avatarUrl").asOpt[String]
      Some(User(username, name, this.name, description, profileImage))
    } catch {
      case _ => {
        Logger.error("Error during fetching user details")
        None
      }
    }
  }
  
  /*
  {"stats":{"v":0,"uv":0,"uvp":0,"vp":0,"lastAcceptedPostDate":0},
    "serverTime":1351791798382,"success":true,
  "user":{
    "sharers":[{"sharerName":"Twitter","sharerId":"twitter","name":"skimbo34","cnxId":818381,"mustSpecifyShareText":true}],
    "smallAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUseQd8mYGSgeObMoJ9g22u2k=",
    "id":1059335,
    "largeAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsdMnNnGAhZeqrqhZZyJYDGM=",
    "avatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsSN_uXZbW2oNh6e4UjnxoeI=",
    "name":"Skimbo",
    "curatedTopics":[{"tags":[],"imageUrl":"http://www.scoop.it/resources/img/theme/noimage.png",
      "stats":{"v":0,"followers":0,"creatorName":"Skimbo","uv":0,"uvp":0,"creatorId":1059335,"vp":0,"createdDate":1350731729000},
      "aggregationImageUrl":"http://www.scoop.it/resources/img/theme/noimage.png",
      "backgroundRepeat":false,"isCurator":true,
      "smallImageUrl":"http://www.scoop.it/resources/img/theme/noimage.png","url":"http://www.scoop.it/t/creation-du-compte",
      "creator":{"smallAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUseQd8mYGSgeObMoJ9g22u2k=",
        "id":1059335,"largeAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsdMnNnGAhZeqrqhZZyJYDGM=",
        "avatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsSN_uXZbW2oNh6e4UjnxoeI=",
        "name":"Skimbo","shortName":"skimbo","url":"http://www.scoop.it/u/skimbo",
        "mediumAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsey7_ZqweuUDvdarVjaIR5c="},
      "id":1009199,"description":"","name":"Création du compte",
      "largeImageUrl":"http://www.scoop.it/resources/img/theme/noimage.png","curatedPostCount":0,
      "mediumImageUrl":"http://www.scoop.it/resources/img/theme/noimage.png","shortName":"creation-du-compte"}],
    "shortName":"skimbo","followedTopics":[],"url":"http://www.scoop.it/u/skimbo",
    "mediumAvatarUrl":"http://img.scoop.it/S8BrlHkXszeJmXBNdRNUsey7_ZqweuUDvdarVjaIR5c="}}*/

}