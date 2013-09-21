package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
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
        "args" -> BSONDocument(args))
    }
    BSONDocument(
      "title" -> BSONString(column.title),
      "unifiedRequests" -> BSONArray(unifiedRequests),
      "index" -> BSONInteger(column.index),
      "width" -> BSONInteger(column.width),
      "height" -> BSONInteger(column.height))
  }

  def fromBSON(c: BSONDocument) = {
    val unifiedRequests = UtilBson.tableTo[UnifiedRequest](c, "unifiedRequests", { r =>
      val requestArgs = r.getAs[BSONDocument]("args").get
      val args = requestArgs.elements.toList.map(n => (n._1, requestArgs.getAs[String](n._1).get)).toMap
      UnifiedRequest(r.getAs[String]("service").get, if (args.nonEmpty) Some(args) else None)
    })
    Column(c.getAs[String]("title").get, unifiedRequests,
      c.getAs[Int]("index").get, c.getAs[Int]("width").get, c.getAs[Int]("height").get)
  }

}