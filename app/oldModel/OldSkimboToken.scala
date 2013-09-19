package oldModel

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._

case class OldSkimboToken(
  token: String,
  secret: Option[String] = None
)

object OldSkimboToken {

  def fromBSON(document: BSONDocument): Option[OldSkimboToken] = {
    Some(OldSkimboToken(
      document.getAs[String]("token").get,
      document.getAs[String]("secret")
    ))
  }
  
}