package parser.json.providers

import org.joda.time.DateTime

import models.Skimbo
import parser.json.GenericJsonParser
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import services.auth.providers.BetaSeries

case class BetaseriesPlanningMessage(
  date: Long,
  show: String,
  url: String,
  number: String,
  title: String)

object BetaseriesPlanningParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[BetaseriesPlanningMessage](json).fold(
      error => logParseError(json, error, "BetaseriesPlanningMessage"),
      e => Some(Skimbo(
        e.show,
        e.show,
        e.number + ":" + e.title,
        new DateTime(e.date * 1000l),
        Nil,
        0,
        Some("http://www.betaseries.com/serie/" + e.url + "/" + e.number),
        e.date.toString,
        None,
        BetaSeries)))
  }

  override def cut(json: JsValue) = {
    val array =
      JsArray(
        Seq(
          // TODO : smell not good, please help me to make better
          json \ "root" \ "planning" \ "0",
          json \ "root" \ "planning" \ "1",
          json \ "root" \ "planning" \ "2",
          json \ "root" \ "planning" \ "3",
          json \ "root" \ "planning" \ "4",
          json \ "root" \ "planning" \ "5",
          json \ "root" \ "planning" \ "6",
          json \ "root" \ "planning" \ "7",
          json \ "root" \ "planning" \ "8",
          json \ "root" \ "planning" \ "9",
          json \ "root" \ "planning" \ "10"))

    super.cut(array)
  }

  override def nextSinceId(sinceId: String, compareSinceId: String): String = {
    if (compareSinceId.isEmpty()) {
      sinceId
    } else {
      if ((sinceId compareTo compareSinceId) > 1) {
        sinceId
      } else {
        compareSinceId
      }
    }
  }

}

object BetaseriesPlanningMessage {
  implicit val reader: Reads[BetaseriesPlanningMessage] = (
    (__ \ "date").read[Long] and
    (__ \ "show").read[String] and
    (__ \ "url").read[String] and
    (__ \ "number").read[String] and
    (__ \ "title").read[String])(BetaseriesPlanningMessage.apply _)
}