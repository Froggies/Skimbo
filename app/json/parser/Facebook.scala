package json.parser

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import json.Skimbo
import services.auth.providers.Facebook

case class FacebookWallMessage(
  id: String,
  fromName: String,
  fromId: Long,
  msgType: String,
  statusType: Option[String],
  nbLikes: Int,
  nbComments: Int,
  createdAt: DateTime,
  link: Option[String],
  message: Option[String],
  story: Option[String],
  picture: Option[String])

object FacebookWallParser extends GenericParser {

  override def asSkimbo(json:JsValue): Option[Skimbo] = {
    val e = Json.fromJson[FacebookWallMessage](json).get
    if (e.message.isDefined || e.story.isDefined) {
      Some(Skimbo(
        e.fromName,
        e.fromName,
        generateMessage(e).get,
        e.createdAt,
        Nil,
        e.nbLikes,
        e.link,
        (e.createdAt.getMillis() / 1000).toInt.toString,
        e.picture,
        Facebook))
    } else {
      None
    }
  }

  override def cut(json: JsValue): List[JsValue] = {
    (json \ "data").as[List[JsValue]]
  }

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
    (__ \ "status_type").readOpt[String] and
    (__ \ "likes" \ "count").readOpt[Int].map(e => e.getOrElse(0)) and
    (__ \ "comments" \ "count").readOpt[Int].map(e => e.getOrElse(0)) and
    (__ \ "created_time").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")) and
    ((__ \ "actions")(0) \\ "link").readOpt[String] and
    (__ \ "message").readOpt[String] and
    (__ \ "story").readOpt[String] and
    (__ \ "picture").readOpt[String])(FacebookWallMessage.apply _)
}