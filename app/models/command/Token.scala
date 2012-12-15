package models.command

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Writes
import services.auth.GenericProvider

case class TokenInvalid(providerName: String)

object TokenInvalid {
  implicit val tokenInvalidWrites = new Writes[TokenInvalid] {
    def writes(t: TokenInvalid) = {
      Json.toJson(Command("tokenInvalid", Some(Json.obj("providerName" -> t.providerName))))
    }
  }
}

object NewToken {
  def asCommand(provider: GenericProvider) = {
    Command("newToken", Some(Json.obj(
      "providerName" -> provider.name)))
  }
}