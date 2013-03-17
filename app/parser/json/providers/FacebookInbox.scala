package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.GenericParser
import models.Skimbo
import parser.json.GenericJsonParser
import services.auth.providers.Facebook
import services.endpoints.Configuration

case class FacebookInboxMessage(
  id: String,
  updatedAt: DateTime,
  users: Seq[FacebookUser],
  data: Option[Seq[FacebookInboxData]] = None
)

case class FacebookUser(
  id:String,
  name:String
)

case class FacebookInboxData(
  from: Option[FacebookUser],
  message: Option[String]
)

object FacebookInboxParser extends GenericJsonParser {

  override def asSkimbo(json:JsValue): Option[Skimbo] = {
    Json.fromJson[FacebookInboxMessage](json).fold(
      error => logParseError(json, error, "FacebookInboxMessage"),
      e => Some(Skimbo(
          e.id,
          getFrom(e.users),
          getFrom(e.users),
          generateMessage(e),
          e.updatedAt,
          Nil,
          -1,
          Some("http://www.facebook.com/messages/"+e.id),
          (e.updatedAt.getMillis() / 1000).toInt.toString,
          None,
          Configuration.Facebook.message))
    )
  }

  override def cut(json: JsValue) = super.cut(json \ "data")

  def getFrom(users:Seq[FacebookUser]) = {
    if(users.size > 1) {
      users(1).name
    } else if(users.size > 0) {
      users.head.name
    } else {
      "private" // in case multi users chat with a friend of friend but not direct friend
    }
  }
  
  def generateMessage(e: FacebookInboxMessage) = {
    val res = new StringBuilder
    e.data.map { data =>
      res ++= "<div><ul>"
      data.map { msg =>
        res ++= "<li>"
        res ++= msg.from.map(_.name).getOrElse("private")
        res ++= " : "
        res ++= msg.message.getOrElse("")
        res ++= "</li>"
      }
      res ++= "</ul></div>"
    }.getOrElse(res ++= "Has wrote new message for you !")
    res toString
  }
  
}

object FacebookUser {
  implicit val facebookuserReader: Reads[FacebookUser] = (
    (__ \ "id").read[String] and
    (__ \ "name").read[String])(FacebookUser.apply _)
}

object FacebookInboxData {
  implicit val facebookDataReader: Reads[FacebookInboxData] = (
    (__ \ "from").readNullable[FacebookUser] and
    (__ \ "message").readNullable[String])(FacebookInboxData.apply _)
}

object FacebookInboxMessage {
  implicit val facebookInBoxReader: Reads[FacebookInboxMessage] = (
    (__ \ "id").read[String] and
    (__ \ "updated_time").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ssZZ")) and
    (__ \ "to" \ "data").read[Seq[FacebookUser]] and
    (__ \ "comments" \ "data").readNullable[Seq[FacebookInboxData]])(FacebookInboxMessage.apply _)
}