package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import org.joda.time.DateTime

case class SkimboToken(
  token: String,
  secret: Option[String] = None,
  refreshToken: Option[String] = None,
  dateEnd: Option[Long] = None)

object SkimboToken {
  implicit val writer = (
    (__ \ "token").write[String] and
    (__ \ "secret").write[Option[String]] and
    (__ \ "refreshToken").write[Option[String]] and
    (__ \ "dateEnd").write[Option[Long]])(unlift(SkimboToken.unapply))

  def toBSON(token: SkimboToken) = {
    if(token.refreshToken.isDefined) {
      BSONDocument(
        "token" -> BSONString(token.token),
        "secret" -> BSONString(token.secret.getOrElse("")),
        "refreshToken" -> BSONString(token.refreshToken.getOrElse("")),
        "dateEnd" -> BSONLong(token.dateEnd.getOrElse(DateTime.now().plusYears(1).getMillis())))
    } else {
      BSONDocument(
        "token" -> BSONString(token.token),
        "secret" -> BSONString(token.secret.getOrElse("")))
    }
  }
  
  def fromBSON(document: BSONDocument): Option[SkimboToken] = {
    Some(SkimboToken(
      document.getAs[String]("token").get,
      document.getAs[String]("secret"),
      document.getAs[String]("refreshToken"),
      document.getAs[Long]("dateEnd")
    ))
  }
}