package models.command

import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import services.auth.GenericProvider

case class Error(providerName: String, msg: String, columnName: Option[String] = None)

object Error {
  implicit val errorWrites = new Writes[Error] {
    def writes(e: Error): JsValue = {
      val json = e.columnName.map(_ => Json.obj(
        "providerName" -> e.providerName,
        "msg" -> e.msg,
        "columnName" -> e.columnName.get))
        .getOrElse(Json.obj(
          "providerName" -> e.providerName,
          "msg" -> e.msg))
      Json.toJson(Command("error", Some(json)))
    }
  }
}