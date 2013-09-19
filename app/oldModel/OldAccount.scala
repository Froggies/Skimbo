package oldModel

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class OldAccount(
  id: String,
  lastUse: Date
)

object OldAccount {

  def fromBSON(a: BSONDocument) = {
    val lastUse = new Date(a.getAs[BSONDateTime]("lastUse").get.value)
    OldAccount(a.getAs[String]("id").get, lastUse)
  }
  
}