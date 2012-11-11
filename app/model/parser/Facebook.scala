package model.parser

import org.joda.time.DateTime
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import model.Skimbo
import model.SocialNetwork


case class FacebookWallMessage(
    id: String,
    fromName: String,
    fromId: Long,
    msgType : String,
    statusType : Option[String],
    nbLikes: Int,
    nbComments: Int,
    createdAt: DateTime,
    link: Option[String],
    message: Option[String],
    story: Option[String]
)

object FacebookWallParser extends GenericParser[FacebookWallMessage] {

  override def from(json: JsValue)(implicit fjs: Reads[FacebookWallMessage]): List[FacebookWallMessage] =
    (json \ "data").as[List[FacebookWallMessage]]

  override def from(json: String)(implicit fjs: Reads[FacebookWallMessage]) = from(Json.parse(json))

  override def asSkimbos(elements: List[FacebookWallMessage]): List[Skimbo] = {
    for (e <- elements if (e.message.isDefined || e.story.isDefined)) yield Skimbo(
      e.fromName,
      e.fromName,
      generateMessage(e).get,
      e.createdAt,
      Nil,
      e.nbLikes,
      e.link,
      (e.createdAt.getMillis()/1000).toInt.toString,
      SocialNetwork.Facebook
    )
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
    ((__ \"actions")(0) \\"link").readOpt[String] and
    (__ \ "message").readOpt[String] and
    (__ \ "story").readOpt[String])(FacebookWallMessage.apply _)
}