package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.GenericParser
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.Facebook
import parser.json.PathDefaultReads
import services.endpoints.Configuration

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
  
  val imageUrl = "https://graph.facebook.com/:id/picture"
  
  override def asSkimbo(json:JsValue): Option[Skimbo] = {
    Json.fromJson[FacebookWallMessage](json).fold(
      error => logParseError(json, error, "FacebookWallMessage"),
      e => if (e.message.isDefined || e.story.isDefined) {
        Some(Skimbo(
          e.id,
          e.fromName,
          e.fromName,
          generateMessage(e).get,
          e.createdAt,
          Nil,
          e.nbLikes,
          generateLink(e),
          (e.createdAt.getMillis() / 1000).toInt.toString,
          Some(imageUrl.replace(":id", e.fromId.toString)),
          Configuration.Facebook.wall,
          false,
          e.picture.map(Seq(_)).getOrElse(Seq.empty),
          //TODO dangerous !! longlongurl_s.xxx to longlongurl_n.xxx ==> find better way
          e.picture.map(img => Seq(img.replaceAll("_s", "_n"))).getOrElse(Seq.empty)))
      } else {
        None
      }
    )
  }

  override def cut(json: JsValue) = super.cut(json \ "data")

  def generateMessage(e: FacebookWallMessage) = {
    e.message.orElse(e.story)
  }
  
  def generateLink(e: FacebookWallMessage) = {
    if(e.link.isEmpty) {
      val ids = e.id.split("_")
      if(ids.length > 1) {
        Some("http://www.facebook.com/"+ids(0)+"/posts/"+ids(1))
      } else if(!ids.isEmpty) {
        Some("http://www.facebook.com/"+ids(0))
      } else {
        Some("http://www.facebook.com/")
      }
    } else {
      Some(e.link)
    }
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
    PathDefaultReads.default((__ \ "comments" \ "count"), 0) and
    (__ \ "created_time").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")) and
    PathDefaultReads.default(((__ \ "actions")(0) \\ "link"), "") and
    (__ \ "message").readNullable[String] and
    (__ \ "story").readNullable[String] and
    (__ \ "picture").readNullable[String])(FacebookWallMessage.apply _)
}