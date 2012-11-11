package model.command

import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._

case class Command(name:String, body:Option[JsValue] = None) {
  override def equals(other:Any) = other match {
    case that:Command => that.name == name
    case _ => false
  }
}

object Command {
  
  implicit val commandReads: Reads[Command] = (
    (__ \ "name").read[String] and
    (__ \ "body").read[Option[JsValue]]
  )(Command.apply _)
  
  implicit val commandWrites = new Writes[Command] {
    def writes(c: Command): JsValue = {
      if(c.body.isDefined) {
        Json.obj(
          "cmd" -> c.name,
          "body" -> c.body.get
        )
      } else {
        Json.obj(
          "cmd" -> c.name
        )
      }
    }
  }
}