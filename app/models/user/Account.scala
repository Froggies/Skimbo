package models.user

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._

case class Account(
  id: String,
  lastUse: Date)
  
object Account {
  
  implicit val writes = new Writes[Account] {
    def writes(a: Account): JsValue = {
      Json.obj(
        "id" -> a.id,
        "lastUser" -> a.lastUse
      )
    }
  }
  
  def toBSON(account:Account) = {
    BSONDocument( 
      "id" -> BSONString(account.id),
      "lastUse" -> BSONDateTime(account.lastUse.getTime()))
  }
}