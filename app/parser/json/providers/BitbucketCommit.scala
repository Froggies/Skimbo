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

case class BitbucketCommit (
  node: String,
  message: String,
  author: String,
  longNode: String,
  date: DateTime
)

object BitbucketCommitParser extends GenericJsonParser {

  val urlRepo = "https://bitbucket.org/:ownerslug/commits/:id"
  
  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    //println("BITBUCKET == "+json.toString)
    Json.fromJson[BitbucketCommit](json).fold(
      error => logParseError(json, error, "BitbucketCommitParser"),
      e => Some(Skimbo(
        e.node,
        e.author,
        e.author,
        e.message,
        e.date,
        Nil,
        -1,
        Some(""),//urlRepo.replace(":ownerslug", ??).replace(":id", e.node),
        e.date.toDate().getTime().toString,
        None,
        Configuration.Bitbucket.commits)))
  }

  override def cut(json: JsValue) = {
    super.cut((json \ "changesets"))
  }

}

object BitbucketCommit {
  
  implicit val reader: Reads[BitbucketCommit] = (
    (__ \ "node").read[String] and
    (__ \ "message").read[String] and
    (__ \ "author").read[String] and
    (__ \ "raw_node").read[String] and
    (__ \ "utctimestamp").read[DateTime](Reads.jodaDateReads(BitbucketEventsRepo.datePattern))
  )(BitbucketCommit.apply _)
}