package models.command

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

case class Command(name: String, body: Option[JsValue] = None) {
  override def equals(other: Any) = other match {
    case that: Command => that.name == name
    case _ => false
  }
}

object Command {

  implicit val commandReads: Reads[Command] = (
    (__ \ "cmd").read[String] and
    (__ \ "body").readOpt[JsValue])(Command.apply _)

  implicit val commandWrites = new Writes[Command] {
    def writes(c: Command): JsValue = {
      c.body.map(_ =>
        Json.obj("cmd" -> c.name, "body" -> c.body.get))
        .getOrElse(
          Json.obj("cmd" -> c.name))
    }
  }
}