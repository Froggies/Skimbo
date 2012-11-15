package model.command
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json

case class TokenInvalid(providerName:String)

object TokenInvalid {
  implicit val tokenInvalidWrites = new Writes[TokenInvalid] {
    def writes(t: TokenInvalid): JsValue = {
      val json = Json.obj(
        "providerName" -> t.providerName
      )
      Json.toJson(Command("tokenInvalid", Some(json)))
    }
  }
}