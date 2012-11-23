package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import services.endpoints.JsonRequest._
import reactivemongo.bson._

case class Column(
  title: String,
  unifiedRequests: Seq[UnifiedRequest]) {

  override def equals(other:Any) = other match {
    case that:Column => that.title == title
    case _ => false
  }
}

object Column {
  
  implicit val reader = (
      (__ \ "title").read[String] and
      (__ \ "unifiedRequests").read[Seq[UnifiedRequest]]
    )(Column.apply _)
  
  implicit val writer = (
	  (__ \ "title").write[String] and
	  (__ \ "unifiedRequests").write[Seq[UnifiedRequest]]
	)(unlift(Column.unapply))
	
  def toBSON(column:Column) = {
    val unifiedRequests:Seq[BSONDocument] = column.unifiedRequests.map { unifiedRequest =>
      val args:Seq[(String, BSONString)] = unifiedRequest.args.get.mapValues(BSONString(_)).toSeq
      BSONDocument(
        "service" -> BSONString(unifiedRequest.service),
        "args" -> BSONDocument(args : _*))
    }
    BSONDocument(
        "title" -> BSONString(column.title),
        "unifiedRequests" -> BSONArray(unifiedRequests : _*))
  }
}