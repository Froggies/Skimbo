package parser.json.providers

import org.joda.time.DateTime

import models.Skimbo
import models.user.ProviderUser
import parser.json.GenericJsonParser
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import services.auth.providers.Bitbucket
import services.endpoints.Configuration

case class BitbucketEventsRepo(
  node: Option[String],
  description: Option[String],
  event: String,
  date:DateTime,
  repo: Option[BitbucketRepo]
)

case class BitbucketRepo(
  logo: String,
  owner: String,
  slug: String
)

object BitbucketEventsRepoParser extends GenericJsonParser {

  val urlRepo = "https://bitbucket.org/:owner/:slug"
  
  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val user = BitbucketUser.asProviderUser(json, None).getOrElse(ProviderUser("", Bitbucket.name, None))
    Json.fromJson[BitbucketEventsRepo](json).fold(
      error => logParseError(json, error, "BitbucketEventsRepo"),
      e => Some(Skimbo(
        e.node.getOrElse(e.date.toDate().getTime().toString),
        user.username.getOrElse("-- private --"),
        user.name.getOrElse("-- private --"),
        e.event + e.repo.map(repo => " on " + repo.owner + "/" + repo.slug).getOrElse(""),
        e.date,
        Nil,
        -1,
        e.repo.map(repo => urlRepo.replace(":owner", repo.owner).replace(":slug", repo.slug)),
        e.date.toDate().getTime().toString,
        user.avatar.orElse(e.repo.map(_.logo)),
        Configuration.Bitbucket.eventsRepo)))
  }

  override def cut(json: JsValue) = {
    super.cut((json \ "events"))
  }

}

object BitbucketRepo {
  implicit val reader: Reads[BitbucketRepo] = (
    (__ \ "logo").read[String] and
    (__ \ "owner").read[String] and
    (__ \ "slug").read[String]
  )(BitbucketRepo.apply _)
}

object BitbucketEventsRepo {
  
  val datePattern = "yyyy-MM-dd HH:mm:ss'+00:00'"
  
  implicit val reader: Reads[BitbucketEventsRepo] = (
    (__ \ "node").readNullable[String] and
    (__ \ "description").readNullable[String] and
    (__ \ "event").read[String] and
    (__ \ "utc_created_on").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "repository").readNullable[BitbucketRepo])(BitbucketEventsRepo.apply _)
}