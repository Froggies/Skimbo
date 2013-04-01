package parser.json.providers

import org.joda.time.DateTime

import models.Skimbo
import parser.json.GenericJsonParser
import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Reads
import play.api.libs.json.__
import services.endpoints.Configuration

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
        e.date,
        e.login,
        e.login,
        e.text,
        new DateTime(e.date.toLong * 1000l),
        Nil,
        -1,
        buildUrl(e),
        e.date,
        None,
        Configuration.BetaSeries.timeline)))
  }

  def buildUrl(e: BetaseriesTimelineMessage) = {
    e.betatype match {
      case "add_serie" => Some("http://www.betaseries.com/serie/" + e.dataUrl.getOrElse(""))
      case "markas" => Some("http://www.betaseries.com/serie/" + e.dataUrl.getOrElse("") + "/" + e.dataNumber.getOrElse(""))
      case _ => None
    }
  }

  override def cut(json: JsValue) = {
    val array = JsArray((json \ "root" \ "timeline").asInstanceOf[JsObject].values.toSeq)
    super.cut(array)
  }

  override def nextSinceId(sinceId: String, compareSinceIdOpt: Option[String]): String = BetaseriesPlanningParser.nextSinceId(sinceId, compareSinceIdOpt)

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