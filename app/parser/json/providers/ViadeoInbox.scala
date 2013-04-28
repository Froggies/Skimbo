package parser.json.providers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.Skimbo
import services.auth.providers.Viadeo
import parser.json.GenericJsonParser
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import parser.json.PathDefaultReads
import services.endpoints.Configuration

case class ViadeoInbox(
  id:String,
  typeViadeo:String,
  link: String,
  read: Boolean,
  subjectMsg: String,
  contentMsg: String,
  fromName:String,
  avatar: String, 
  updatedTime:DateTime
)
    
object ViadeoInboxParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[ViadeoInbox](json).fold(
      error => logParseError(json, error, "ViadeoInbox"),
      msg => Some(Skimbo(
        msg.id,
        msg.fromName,
        msg.fromName,
        msg.subjectMsg + "\n\n" + msg.contentMsg,
        msg.updatedTime,
        Nil,
        -1,
        Some(msg.link),
        msg.updatedTime.toString(ViadeoWallMessage.datePattern),
        Some(msg.avatar),
        Configuration.Viadeo.smartNews)))
  }
  
  override def cut(json: JsValue): List[JsValue] = ViadeoWallParser.cut(json)
  
  override def nextSinceId(sinceId: String, compareSinceIdOpt: Option[String]): String = 
    ViadeoWallParser.nextSinceId(sinceId, compareSinceIdOpt)

}

object ViadeoInbox {
  
  implicit val viadeoReader: Reads[ViadeoInbox] = (
    (__ \ "id").read[String] and
    (__ \ "type").read[String] and
    (__ \ "link").read[String] and
    (__ \ "read").read[Boolean] and
    (__ \ "subject").read[String] and
    (__ \ "message").read[String] and
    (__ \ "from" \ "name").read[String] and
    (__ \ "from" \ "picture_medium").read[String] and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads(ViadeoWallMessage.datePattern)))(ViadeoInbox.apply _)
}
