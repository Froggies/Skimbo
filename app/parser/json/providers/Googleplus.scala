package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.joda.time.format.DateTimeFormat
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.GooglePlus
import services.endpoints.Configuration

case class GoogleplusWallMessage(
  id:String,
  displayName:String,
  title:String,
  publishedDate:DateTime,
  plusoners:Int,
  url:Option[String],
  actorImage:Option[String]
)

object GoogleplusWallParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[GoogleplusWallMessage](json).fold(
      error => logParseError(json, error, "GoogleplusWallParser"),
      e => Some(Skimbo(
        e.id,
        e.displayName,
        e.displayName,
        e.title,
        e.publishedDate,
        Nil,
        e.plusoners,
        e.url,
        e.publishedDate.toString(GoogleplusWallMessage.datePattern),
        e.actorImage,
        Configuration.GooglePlus.wall)))
  }
  
  override def cut(json: JsValue): List[JsValue] = super.cut(json \ "items")
  
  override def nextSinceId(sinceId: String, compareSinceIdOpt: Option[String]): String = {
    val date = DateTime.parse(sinceId, DateTimeFormat.forPattern(GoogleplusWallMessage.datePattern))
    if (compareSinceIdOpt.isEmpty /* == None */) {
      date.toString(GoogleplusWallMessage.datePattern)
    } else {
      val date1 = DateTime.parse(compareSinceIdOpt.get, DateTimeFormat.forPattern(GoogleplusWallMessage.datePattern))
      if (date.isAfter(date1)) {
        date.toString(GoogleplusWallMessage.datePattern)
      } else {
        date1.toString(GoogleplusWallMessage.datePattern)
      }
    }
  }
}

object GoogleplusWallMessage {
  
  val datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
  
  implicit val googlePlusReader: Reads[GoogleplusWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "actor" \ "displayName").read[String] and
    (__ \ "title").read[String] and
    (__ \ "published").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "object" \ "plusoners" \ "totalItems").read[Int] and
    (__ \ "url").readNullable[String] and
    (__ \ "actor" \ "image" \ "url").readNullable[String])(GoogleplusWallMessage.apply _)
    
}

