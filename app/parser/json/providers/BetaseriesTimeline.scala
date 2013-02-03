package parser.json.providers

import org.joda.time.DateTime
import models.Skimbo
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsArray
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import services.auth.providers.BetaSeries
import parser.json.GenericJsonParser

case class BetaseriesTimelineMessage(
  date: String,
  text: String,
  betatype: String,
  ref: String,
  login: String,
  dataUrl: Option[String],
  dataNumber: Option[String],
  dataTitle: Option[String])

object BetaseriesTimelineParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[BetaseriesTimelineMessage](json).fold(
      error => logParseError(json, error, "BetaseriesTimelineMessage"),
      e => Some(Skimbo(
        e.login,
        e.login,
        e.text,
        new DateTime(e.date.toLong * 1000l),
        Nil,
        0,
        buildUrl(e),
        e.date,
        None,
        BetaSeries)))
  }

  def buildUrl(e: BetaseriesTimelineMessage) = {
    e.betatype match {
      case "add_serie" => Some("http://www.betaseries.com/serie/" + e.dataUrl.getOrElse(""))
      case "markas" => Some("http://www.betaseries.com/serie/" + e.dataUrl.getOrElse("") + "/" + e.dataNumber.getOrElse(""))
      case _ => None
    }
  }

  override def cut(json: JsValue) = {
    val array =
      JsArray(
        Seq(
          // TODO : smell not good, please help me to make better
          json \ "root" \ "timeline" \ "0",
          json \ "root" \ "timeline" \ "1",
          json \ "root" \ "timeline" \ "2",
          json \ "root" \ "timeline" \ "3",
          json \ "root" \ "timeline" \ "4",
          json \ "root" \ "timeline" \ "5",
          json \ "root" \ "timeline" \ "6",
          json \ "root" \ "timeline" \ "7",
          json \ "root" \ "timeline" \ "8",
          json \ "root" \ "timeline" \ "9",
          json \ "root" \ "timeline" \ "10",
          json \ "root" \ "timeline" \ "11",
          json \ "root" \ "timeline" \ "12",
          json \ "root" \ "timeline" \ "13",
          json \ "root" \ "timeline" \ "14",
          json \ "root" \ "timeline" \ "15",
          json \ "root" \ "timeline" \ "16",
          json \ "root" \ "timeline" \ "17",
          json \ "root" \ "timeline" \ "18",
          json \ "root" \ "timeline" \ "19"))

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

object BetaseriesTimelineMessage {
  implicit val reader: Reads[BetaseriesTimelineMessage] = (
    (__ \ "date").read[String] and
    (__ \ "html").read[String] and
    (__ \ "type").read[String] and
    (__ \ "ref").read[String] and
    (__ \ "login").read[String] and
    (__ \ "data" \ "url").readNullable[String] and
    (__ \ "data" \ "number").readNullable[String] and
    (__ \ "data" \ "title").readNullable[String])(BetaseriesTimelineMessage.apply _)
}