package json.parser

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import json.Skimbo
import services.auth.providers.GooglePlus
import org.joda.time.format.DateTimeFormat

case class GoogleplusWallMessage(
  id:String,
  displayName:String,
  title:String,
  publishedDate:DateTime,
  plusoners:Int,
  url:Option[String],
  actorImage:Option[String]
    )

object GoogleplusWallParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val e = Json.fromJson[GoogleplusWallMessage](json).get
    Some(Skimbo(
      e.displayName,
      e.displayName,
      e.title,
      e.publishedDate,
      Nil,
      e.plusoners,
      e.url,
      e.publishedDate.toString(GoogleplusWallMessage.datePattern),
      e.actorImage,
      GooglePlus))
  }
  
  override def cut(json: JsValue): List[JsValue] = {
    (json \ "items").as[List[JsValue]]
  }
  
  override def nextSinceId(sinceId:String, sinceId2:String): String = {
    val date = DateTime.parse(sinceId, DateTimeFormat.forPattern(GoogleplusWallMessage.datePattern))
    if(sinceId2.isEmpty()) {
      date.toString(GoogleplusWallMessage.datePattern)
    } else {
      val date1 = DateTime.parse(sinceId2, DateTimeFormat.forPattern(GoogleplusWallMessage.datePattern))
      if(date.isAfter(date1)) {
        date.toString(GoogleplusWallMessage.datePattern)
      } else {
        date1.toString(GoogleplusWallMessage.datePattern)
      }
    }
  }
}

object GoogleplusWallMessage {
  
  val datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  
  implicit val githubReader: Reads[GoogleplusWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "actor" \ "displayName").read[String] and
    (__ \ "title").read[String] and
    (__ \ "published").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "object" \ "plusoners" \ "totalItems").read[Int] and
    (__ \ "url").readOpt[String] and
    (__ \ "object" \ "actor" \ "image" \ "url").readOpt[String])(GoogleplusWallMessage.apply _)
    
}

