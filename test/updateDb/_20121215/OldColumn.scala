package updateDb._20121215

import play.api.libs.json._
import play.api.libs.functional.syntax._
import services.endpoints.JsonRequest._
import reactivemongo.bson._
import play.api.libs.json.JsNumber
import services.dao.UtilBson

case class OldColumn(
  title: String,
  unifiedRequests: Seq[UnifiedRequest]) {

  override def equals(other: Any) = other match {
    case that: OldColumn => that.title == title
    case _ => false
  }
}

object OldColumn {

  def toBSON(column: OldColumn) = {
    val unifiedRequests: Seq[BSONDocument] = column.unifiedRequests.map { unifiedRequest =>
      val args: Seq[(String, BSONString)] = unifiedRequest.args.getOrElse(Map.empty).mapValues(BSONString(_)).toSeq
      BSONDocument(
        "service" -> BSONString(unifiedRequest.service),
        "args" -> BSONDocument(args: _*))
    }
    BSONDocument(
      "title" -> BSONString(column.title),
      "unifiedRequests" -> BSONArray(unifiedRequests: _*),
      "index" -> BSONInteger(0),
      "width" -> BSONInteger(-1),
      "height" -> BSONInteger(-1))
  }

  def fromBSON(c: TraversableBSONDocument) = {
    val unifiedRequests = UtilBson.tableTo[UnifiedRequest](c, "unifiedRequests", { r =>
      val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable
      val args = requestArgs.mapped.map(requestArg =>
        (requestArg._1, requestArgs.getAs[BSONString](requestArg._1).get.value))
      UnifiedRequest(UtilBson.asString(r, "service"), if (args.nonEmpty) Some(args) else None)
    })
    OldColumn(UtilBson.asString(c, "title"), unifiedRequests)
  }

}