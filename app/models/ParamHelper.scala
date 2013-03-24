package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ParamHelper(
  display: String,
  call: String,
  avatarUrl: String,
  description: Option[String]
)

object ParamHelper {

  implicit val writer = (
    (__ \ "display").write[String] and
    (__ \ "call").write[String] and
    (__ \ "avatarUrl").write[String] and
    (__ \ "description").write[Option[String]]
  )(unlift(ParamHelper.unapply))
  
  def fromProviderUser(providerUser: models.user.ProviderUser): ParamHelper = {
    ParamHelper(
      providerUser.name.getOrElse(providerUser.username.getOrElse("--no name--")),
      providerUser.id,
      providerUser.avatar.getOrElse(""),
      providerUser.description
    )
  }
  
}