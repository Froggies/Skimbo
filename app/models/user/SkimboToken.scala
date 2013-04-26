package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._

case class SkimboToken(
  token: String,
  secret: Option[String] = None)

object SkimboToken {
  implicit val writer = (
    (__ \ "token").write[String] and
    (__ \ "secret").write[Option[String]])(unlift(SkimboToken.unapply))

  def toBSON(token: SkimboToken) = {
    BSONDocument(
      "token" -> BSONString(token.token),
      "secret" -> BSONString(token.secret.getOrElse("")))
  }
  
  def fromBSON(document: BSONDocument): Option[SkimboToken] = {
    Some(SkimboToken(
      document.getAs[String]("token").get,
      document.getAs[String]("secret")
    ))
  }
}