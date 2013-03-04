package models

import org.joda.time._
import play.api.libs.json._
import services.auth.GenericProvider
import play.api.libs.json.Json.toJsFieldJsValueWrapper

/**
* Common format between social networks
*/
case class Skimbo(
  idProvider:String,
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
        "idProvider" -> skimbo.idProvider,
        "authorName" -> skimbo.authorName,
        "authorScreenName" -> skimbo.authorScreenName,
        "message" -> skimbo.message,
        "createdAt" -> skimbo.createdAt.toDate().getTime().toString(),
        "comments" -> List[String](),
        "shared" -> skimbo.shared,
        "directLink" -> skimbo.directLink,
        "sinceId" -> skimbo.sinceId,
        "authorAvatar" -> skimbo.authorAvatar,
        "from" -> skimbo.from.name
      )
    }
  }
  
}

