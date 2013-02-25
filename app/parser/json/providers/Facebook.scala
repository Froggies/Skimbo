package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.GenericParser
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.Facebook
import parser.json.PathDefaultReads

case class FacebookWallMessage(
  id: String,
  fromName: String,
  fromId: Long,
  msgType: String,
  statusType: Option[String],
  nbLikes: Int,
  nbComments: Int,
  createdAt: DateTime,
  link: String,
  message: Option[String],
  story: Option[String],
  picture: Option[String]
)

object FacebookWallParser extends GenericJsonParser {
  
  override def asSkimbo(json:JsValue): Option[Skimbo] = {
    Json.fromJson[FacebookWallMessage](json).fold(
      error => logParseError(json, error, "FacebookWallMessage"),
      e => if (e.message.isDefined || e.story.isDefined) {
        Some(Skimbo(
          e.fromName,
          e.fromName,
          generateMessage(e).get,
          e.createdAt,
          Nil,
          e.nbLikes,
          Some(e.link),
          (e.createdAt.getMillis() / 1000).toInt.toString,
          e.picture,
          Facebook))
      } else {
        None
      }
    )
  }

  override def cut(json: JsValue) = super.cut(json \ "data")

  def generateMessage(e: FacebookWallMessage) = {
    e.message.orElse(e.story)
  }

}

object FacebookWallMessage {
  implicit val facebookReader: Reads[FacebookWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "from" \ "name").read[String] and
    (__ \ "from" \ "id").read[String].map(_.toLong) and
    (__ \ "type").read[String] and
    (__ \ "status_type").readNullable[String] and
    PathDefaultReads.default((__ \ "likes" \ "count"), 0) and
    (__ \ "comments" \ "count").readNullable[Int].map(e => e.getOrElse(0)) and
    (__ \ "created_time").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")) and
    PathDefaultReads.default(((__ \ "actions")(0) \\ "link"), "") and
    (__ \ "message").readNullable[String] and
    (__ \ "story").readNullable[String] and
    (__ \ "picture").readNullable[String])(FacebookWallMessage.apply _)
}