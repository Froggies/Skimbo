package model

import org.joda.time._
import SocialNetwork._
import scala.util.parsing.json.JSONObject
import play.api.libs.json.JsString
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import services.auth.GenericProvider

/**
* Common format between social networks
*/
case class Skimbo(
  authorName: String,
  authorScreenName: String,
  message: String,
  createdAt: DateTime,
  comments: List[Skimbo],
  shared: Int,
  directLink: Option[String],
  sinceId: String,
  authorAvatar:Option[String],
  from: GenericProvider
)

object Skimbo {
  implicit val writes:Writes[Skimbo] = new Writes[Skimbo] {
    def writes(skimbo: Skimbo): JsValue = {
      Json.obj(
        "authorName" -> skimbo.authorName,
        "authorScreenName" -> skimbo.authorScreenName,
        "message" -> skimbo.message,
        "createdAt" -> skimbo.createdAt.toDate().getTime().toString(),
        "comments" -> skimbo.comments.map(Json.toJson(_)(Skimbo.writes)),
        "shared" -> skimbo.shared,
        "directLink" -> skimbo.directLink,
        "sinceId" -> skimbo.sinceId,
        "authorAvatar" -> skimbo.authorAvatar,
        "from" -> skimbo.from.name
      )
    }
  }
  
}

