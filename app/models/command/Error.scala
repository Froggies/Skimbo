package models.command

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Writes

object ErrorType extends Enumeration {
  type ErrorType = Value
  val RateLimit, Timeout, Unknown, Parser, NoParser, Post, Star, Comment, EmailNotSend = Value
}
import ErrorType._

case class Error(
  providerName: String, 
  errorType: ErrorType, 
  columnName: Option[String] = None
)

object Error {
  implicit val errorWrites = new Writes[Error] {
    def writes(e: Error): JsValue = {
      val json = Json.obj(
        "providerName" -> e.providerName,
        "type" -> e.errorType.toString(),
        "columnName" -> e.columnName)
      Json.toJson(Command("error", Some(json)))
    }
  }
}