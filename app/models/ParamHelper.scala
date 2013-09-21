package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class ParamHelper(
  display: String,
  call: String,
  avatarUrl: String,
  description: Option[String]
)

object ParamHelper {

  implicit val reader: Reads[ParamHelper] = (
    (__ \ "display").read[String] and
    (__ \ "call").read[String] and
    (__ \ "avatarUrl").read[String] and
    (__ \ "description").readNullable[String])(ParamHelper.apply _)
  
  implicit val writer = (
    (__ \ "display").write[String] and
    (__ \ "call").write[String] and
    (__ \ "avatarUrl").write[String] and
    (__ \ "description").write[Option[String]]
  )(unlift(ParamHelper.unapply))
  
  def toBSON(paramHelper: ParamHelper) = {
    BSONDocument(
      "display" -> BSONString(paramHelper.display),
      "call" -> BSONString(paramHelper.call),
      "avatarUrl" -> BSONString(paramHelper.avatarUrl),
      "description" -> BSONString(paramHelper.description.getOrElse(""))
    )
  }

  def fromBSON(c: BSONDocument) = {
    ParamHelper(
      c.getAs[String]("display").get, 
      c.getAs[String]("call").get,
      c.getAs[String]("avatarUrl").get, 
      c.getAs[String]("description")
    )
  }
  
  def fromProviderUser(providerUser: models.user.ProviderUser): ParamHelper = {
    ParamHelper(
      providerUser.name.getOrElse(providerUser.username.getOrElse("--no name--")),
      providerUser.id,
      providerUser.avatar.getOrElse(""),
      providerUser.description
    )
  }
  
}