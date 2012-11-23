package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._

case class ProviderUser(
  id: String,
  socialType: String,
  username: Option[String] = None,
  name: Option[String] = None,
  description: Option[String] = None,
  avatar: Option[String] = None)

object ProviderUser {

  implicit val writer = (
	  (__ \ "id").write[String] and
	  (__ \ "socialType").write[String] and
	  (__ \ "username").write[Option[String]] and
	  (__ \ "name").write[Option[String]] and
	  (__ \ "description").write[Option[String]] and
	  (__ \ "avatar").write[Option[String]]
	)(unlift(ProviderUser.unapply))
	
  def toBSON(distant: ProviderUser) = {
    BSONDocument(
      "id" -> BSONString(distant.id),
      "social" -> BSONString(distant.socialType))
  }
}