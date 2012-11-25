package json.parser

import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._
import json.Skimbo
import services.auth.providers.Viadeo
import org.joda.time.format.DateTimeParser
import org.joda.time.format.DateTimeFormat

case class ViadeoWallMessage(
  id:String,
  typeViadeo:String,
  fromName:String,
  onTitle: Option[String],
  onMessage:Option[String],
  likeCount:Int,
  title: Option[String],
  message:Option[String],
  pictureUrl:Option[String],
  updatedTime:DateTime,
  infeedLink:Option[String]
)
    
object ViadeoWallParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[ViadeoWallMessage](json).fold(
      error => logParseError(json, error, "ViadeoWallMessage"),
      msg => Some(Skimbo(
        msg.fromName,
        msg.fromName,
        generateMsg(msg),
        msg.updatedTime,
        Nil,
        msg.likeCount,
        msg.infeedLink,
        msg.updatedTime.toString(ViadeoWallMessage.datePattern),
        msg.pictureUrl,
        Viadeo))
      )
  }
  
  def generateMsg(e: ViadeoWallMessage) = {
    if(e.message.isDefined && !e.message.get.isEmpty()) {
      e.message.get
    }
    else if(e.onMessage.isDefined && !e.onMessage.get.isEmpty()) {
      e.onMessage.get
    } else if(e.title.isDefined && !e.title.get.isEmpty()) {
      e.title.get
    } else if(e.onTitle.isDefined && !e.onTitle.get.isEmpty()) {
      e.onTitle.get
    } else {
      "Msg not decrypted !"
    }
  }
  
  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "data")
  
  override def nextSinceId(sinceId:String, sinceId2:String): String = {
    val date = DateTime.parse(sinceId, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
    if(sinceId2.isEmpty()) {
      date.plusSeconds(1).toString(ViadeoWallMessage.datePattern)
    } else {
      val date1 = DateTime.parse(sinceId2, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
      if(date.isAfter(date1)) {
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
    ((__ \ "from" \ "name").read[String] or (__ \ "on" \ "from" \ "name").read[String]) and
    (__ \ "on" \ "title").readOpt[String] and
    (__ \ "on" \ "message").readOpt[String] and
    (__ \ "like_count").read[Int] and
    (__ \ "title").readOpt[String] and
    (__ \ "message").readOpt[String] and
    (__ \ "picture").readOpt[String] and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "infeed_link").readOpt[String])(ViadeoWallMessage.apply _)
}