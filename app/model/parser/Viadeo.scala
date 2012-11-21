package model.parser

import org.joda.time.DateTime
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import model.Skimbo
import services.auth.providers.Viadeo

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
      e.updatedTime.toString(),
      e.pictureUrl,
      Viadeo))
  }
  
  override def cut(json: JsValue): List[JsValue] = {
    (json \ "data").as[List[JsValue]]
  }

}

object ViadeoWallMessage {
  implicit val viadeoReader: Reads[ViadeoWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "type").read[String] and
    (__ \ "from" \ "name").read[String] and
    (__ \ "on" \ "message").readOpt[String] and
    (__ \ "like_count").read[Int] and
    (__ \ "message").readOpt[String] and
    (__ \ "picture").readOpt[String] and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")) and
    (__ \ "infeed_link").readOpt[String])(ViadeoWallMessage.apply _)
}