package parser.json.providers

import parser.json.GenericJsonParser
import play.api.libs.json.JsValue
import models.Skimbo
import org.joda.time.DateTime
import services.endpoints.Configuration
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import models.user.ProviderUser
import services.auth.providers.Bitbucket

case class BitbucketEventsRepo(
  node: Option[String],
  description: String,
  event: String,
  betatype: String)

object BitbucketEventsRepoParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    println("BITBUCKET == "+json.toString)
    val user = BitbucketUser.asProviderUser(json, None).getOrElse(ProviderUser("", Bitbucket.name, None))
    Json.fromJson[BitbucketEventsRepo](json).fold(
      error => logParseError(json, error, "BitbucketEventsRepo"),
      e => Some(Skimbo(
        e.node.getOrElse(new DateTime().toString),
        user.username.getOrElse("-- private --"),
        user.name.getOrElse("-- private --"),
        e.event,
        new DateTime(),
        Nil,
        -1,
        None,
        "",
        None,
        Configuration.Bitbucket.eventsRepo)))
  }

  override def cut(json: JsValue) = {
    super.cut((json \ "events"))
  }

}

object BitbucketEventsRepo {
  implicit val reader: Reads[BitbucketEventsRepo] = (
    (__ \ "node").readNullable[String] and
    (__ \ "description").read[String] and
    (__ \ "event").read[String] and
    (__ \ "utc_created_on").read[String])(BitbucketEventsRepo.apply _)
//    (__ \ "login").read[String] and
//    (__ \ "data" \ "url").readNullable[String] and
//    (__ \ "data" \ "number").readNullable[String] and
//    (__ \ "data" \ "title").readNullable[String])(BitbucketEventsRepo.apply _)
}