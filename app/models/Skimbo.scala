package models

import org.joda.time._
import play.api.libs.json._
import services.auth.GenericProvider
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import services.endpoints.EndpointConfig

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
  stared: Int,
  directLink: Option[String],
  sinceId: String,
  authorAvatar:Option[String],
  config: EndpointConfig,
  iStared: Boolean = false,
  picturesMin: Seq[String] = Seq.empty,
  picturesMax: Seq[String] = Seq.empty
)

object Skimbo {
  implicit val writes = new Writes[Skimbo] {
    def writes(skimbo: Skimbo) = {
      val canComment = skimbo.config.commenter.isDefined && !skimbo.idProvider.isEmpty
      Json.obj(
        "idProvider" -> skimbo.idProvider,
        "authorName" -> skimbo.authorName,
        "authorScreenName" -> skimbo.authorScreenName,
        "message" -> skimbo.message,
        "createdAt" -> skimbo.createdAt.toDate().getTime().toString(),
        "comments" -> List[String](),
        "stared" -> skimbo.stared,
        "directLink" -> skimbo.directLink,
        "sinceId" -> skimbo.sinceId,
        "authorAvatar" -> skimbo.authorAvatar,
        "from" -> skimbo.config.provider.name,
        "service" -> skimbo.config.uniqueName,
        "hasDetails" -> skimbo.config.parserDetails.isDefined,
        "canStar" -> skimbo.config.starer.isDefined,
        "iStared" -> skimbo.iStared,
        "canComment" -> canComment,
        "picturesMin" -> skimbo.picturesMin,
        "picturesMax" -> skimbo.picturesMax
      )
    }
  }
  
}

