package models
import java.util.Date
import play.api.libs.json.Writes
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import reactivemongo.bson.BSONArray
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONString
import reactivemongo.bson.BSONDateTime

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