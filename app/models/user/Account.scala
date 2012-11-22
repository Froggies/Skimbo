package models.user

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._

case class Account(
  id: String,
  lastUse: Date)
  
object Account {
  
  implicit val writer = (
	  (__ \ "id").write[String] and
	  (__ \ "lastUse").write[Date]
	)(unlift(Account.unapply))
	
  def toBSON(account:Account) = {
    BSONDocument( 
      "id" -> BSONString(account.id),
      "lastUse" -> BSONDateTime(account.lastUse.getTime()))
  }
}