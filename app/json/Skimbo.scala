package json

import org.joda.time._
import scala.util.parsing.json.JSONObject
import play.api.libs.json._
import services.auth.GenericProvider
import scala.collection.immutable.List

/**
* Common format between social networks
*/
case class Skimbo(
  authorName: String,
  authorScreenName: String,
  message: String,
  createdAt: DateTime,
  comments: List[String],
  shared: Int,
  directLink: Option[String],
  sinceId: String,
  authorAvatar:Option[String],
  from: GenericProvider
)

object Skimbo {
  implicit val writes = new Writes[Skimbo] {
    def writes(skimbo: Skimbo) = {
      Json.obj(
        "authorName" -> skimbo.authorName,
        "authorScreenName" -> skimbo.authorScreenName,
        "message" -> skimbo.message,
        "createdAt" -> skimbo.createdAt.toDate().getTime().toString(),
        "comments" -> List.empty[String],
        "shared" -> skimbo.shared,
        "directLink" -> skimbo.directLink,
        "sinceId" -> skimbo.sinceId,
        "authorAvatar" -> skimbo.authorAvatar,
        "from" -> skimbo.from.name
      )
    }
  }
  
}

