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

case class ViadeoWallMessage(
  id: String,
  typeViadeo: String,
  fromName: String,
  onTitle: String,
  onMessage: String,
  likeCount: Int,
  title: Option[String],
  message: Option[String],
  label: Option[String],
  pictureUrl: Option[String],
  avatarUrl: String,
  updatedTime: DateTime,
  infeedLink: Option[String])

object ViadeoWallParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[ViadeoWallMessage](json).fold(
      error => logParseError(json, error, "ViadeoWallMessage"),
      msg => Some(Skimbo(
        msg.id,
        msg.fromName,
        msg.fromName,
        generateMsg(msg),
        msg.updatedTime,
        Nil,
        msg.likeCount,
        msg.infeedLink,
        msg.updatedTime.toString(ViadeoWallMessage.datePattern),
        Some(msg.avatarUrl),
        Configuration.Viadeo.smartNews,
        false,
        msg.pictureUrl.map(Seq(_)).getOrElse(Seq.empty))))
  }

  def generateMsg(e: ViadeoWallMessage) = {
    if (e.message.isDefined && !e.message.get.isEmpty()) {
      e.label.getOrElse("") + e.message.get
    } else if (!e.onMessage.isEmpty()) {
      e.onMessage
    } else if (e.title.isDefined && !e.title.get.isEmpty()) {
      e.title.get
    } else if (!e.onTitle.isEmpty()) {
      e.onTitle
    } else {
      "Msg not decrypted !"
    }
  }

  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "data")

  override def nextSinceId(sinceId: String, compareSinceIdOpt: Option[String]): String = {
    val date = DateTime.parse(sinceId, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
    if (compareSinceIdOpt.isEmpty) {
      date.plusSeconds(1).toString(ViadeoWallMessage.datePattern)
    } else {
      val date1 = DateTime.parse(compareSinceIdOpt.get, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
      if (date.isAfter(date1)) {
        date.plusSeconds(1).toString(ViadeoWallMessage.datePattern)
      } else {
        date1.plusSeconds(1).toString(ViadeoWallMessage.datePattern)
      }
    }
  }

}

object ViadeoWallMessage {

  val datePattern = "yyyy-MM-dd'T'HH:mm:ssZZ"

  implicit val viadeoReader: Reads[ViadeoWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "type").read[String] and
    ((__ \ "from" \ "name").read[String] or (__ \ "on" \ "from" \ "name").read[String] or (__ \ "type").read[String]) and
    PathDefaultReads.default((__ \ "on" \ "title"), "") and
    PathDefaultReads.default((__ \ "on" \ "message"), "") and
    (__ \ "like_count").read[Int] and
    (__ \ "title").readNullable[String] and
    (__ \ "message").readNullable[String] and
    (__ \ "label").readNullable[String] and
    (__ \ "picture").readNullable[String] and
    PathDefaultReads.default((__ \ "from" \ "picture_medium"), "") and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads(datePattern)) and
    ((__ \ "infeed_link").readNullable[String] or (__ \ "on" \ "link").readNullable[String]))(ViadeoWallMessage.apply _)
}