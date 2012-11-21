package model.parser

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import model.Skimbo
import services.auth.providers.Viadeo
import org.joda.time.format.DateTimeParser
import org.joda.time.format.DateTimeFormat

case class ViadeoWallMessage(
  id:String,
  typeViadeo:String,
  fromName:String,
  onMessage:Option[String],
  likeCount:Int,
  message:Option[String],
  pictureUrl:Option[String],
  updatedTime:DateTime,
  infeedLink:Option[String]
)
    
object ViadeoWallParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val e = Json.fromJson[ViadeoWallMessage](json).get
    Some(Skimbo(
      e.fromName,
      e.fromName,
      e.onMessage.getOrElse(e.message.getOrElse("??")),
      e.updatedTime,
      Nil,
      e.likeCount,
      e.infeedLink,
      e.updatedTime.toString(ViadeoWallMessage.datePattern),
      e.pictureUrl,
      Viadeo))
  }
  
  override def cut(json: JsValue): List[JsValue] = {
    (json \ "data").as[List[JsValue]]
  }
  
  override def nextSinceId(sinceId:String, sinceId2:String): String = {
    val date = DateTime.parse(sinceId, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
    if(sinceId2.isEmpty()) {
      date.plusSeconds(1).toString(ViadeoWallMessage.datePattern)
    } else {
      val date1 = DateTime.parse(sinceId2, DateTimeFormat.forPattern(ViadeoWallMessage.datePattern))
      println("!! parse date ok")
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
    (__ \ "from" \ "name").read[String] and
    (__ \ "on" \ "message").readOpt[String] and
    (__ \ "like_count").read[Int] and
    (__ \ "message").readOpt[String] and
    (__ \ "picture").readOpt[String] and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "infeed_link").readOpt[String])(ViadeoWallMessage.apply _)
}