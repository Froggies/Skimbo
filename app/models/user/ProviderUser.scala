package models.user

import play.api.libs.json._
import reactivemongo.bson._

case class ProviderUser(
  id: String,
  socialType: String,
  username: Option[String] = None,
  name: Option[String] = None,
  description: Option[String] = None,
  avatar: Option[String] = None)

object ProviderUser {

  implicit val writes = new Writes[ProviderUser] {
    def writes(pu: ProviderUser): JsValue = {
      Json.obj(
        "id" -> pu.id,
        "socialType" -> pu.socialType,
        "username" -> pu.username,
        "name" -> pu.name,
        "description" -> pu.description,
        "avatar" -> pu.avatar)
    }
  }

  def toBSON(distant: ProviderUser) = {
    BSONDocument(
      "id" -> BSONString(distant.id),
      "social" -> BSONString(distant.socialType))
  }
}