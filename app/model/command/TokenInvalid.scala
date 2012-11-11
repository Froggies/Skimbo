package model.command
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json

case class TokenInvalid(providerName:String)

object TokenInvalid {
  implicit val tokenInvalidWrites = new Writes[TokenInvalid] {
    def writes(t: TokenInvalid): JsValue = {
      Json.obj(
        "cmd" -> "tokenInvalid",
        "name" -> t.providerName
      )
    }
  }
}