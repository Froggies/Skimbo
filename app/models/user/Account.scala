package models
import java.util.Date
import play.api.libs.json.Writes
import play.api.libs.json.Json
import play.api.libs.json.JsValue

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
}