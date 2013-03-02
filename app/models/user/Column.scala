package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import services.endpoints.JsonRequest._
import reactivemongo.bson._
import services.dao.UtilBson

case class Column(
  title: String,
  unifiedRequests: Seq[UnifiedRequest],
  index: Int,
  width: Int,
  height: Int) {

  override def equals(other: Any) = other match {
    case that: Column => that.title == title
    case _ => false
  }
}

object Column {

  implicit val reader = (
    (__ \ "title").read[String] and
    (__ \ "unifiedRequests").read[Seq[UnifiedRequest]] and
    (__ \ "index").read[Int] and
    (__ \ "width").read[Int] and
    (__ \ "height").read[Int])(Column.apply _)

  implicit val writer = (
    (__ \ "title").write[String] and
    (__ \ "unifiedRequests").write[Seq[UnifiedRequest]] and
    (__ \ "index").write[Int] and
    (__ \ "width").write[Int] and
    (__ \ "height").write[Int])(unlift(Column.unapply))

  def toBSON(column: Column) = {
    val unifiedRequests: Seq[BSONDocument] = column.unifiedRequests.map { unifiedRequest =>
      val args: Seq[(String, BSONString)] = unifiedRequest.args.getOrElse(Map.empty).mapValues(BSONString(_)).toSeq
      BSONDocument(
        "service" -> BSONString(unifiedRequest.service),
        "args" -> BSONDocument(args: _*))
    }
    BSONDocument(
      "title" -> BSONString(column.title),
      "unifiedRequests" -> BSONArray(unifiedRequests: _*),
      "index" -> BSONInteger(column.index),
      "width" -> BSONInteger(column.width),
      "height" -> BSONInteger(column.height))
  }

  def fromBSON(c: TraversableBSONDocument) = {
    val unifiedRequests = UtilBson.tableTo[UnifiedRequest](c, "unifiedRequests", { r =>
      val requestArgs = r.getAs[BSONDocument]("args").get.toTraversable
      val args = requestArgs.mapped.map(requestArg =>
        (requestArg._1, requestArgs.getAs[BSONString](requestArg._1).get.value))
      UnifiedRequest(UtilBson.asString(r, "service"), if (args.nonEmpty) Some(args) else None)
    })
    Column(UtilBson.asString(c, "title"), unifiedRequests,
      UtilBson.asInt(c, "index"), UtilBson.asInt(c, "width"), UtilBson.asInt(c, "height"))
  }

}