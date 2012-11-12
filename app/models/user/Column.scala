package models.user

import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import services.endpoints.JsonRequest._
import reactivemongo.bson.BSONArray
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONString

case class Column(
  title: String,
  unifiedRequests:Seq[UnifiedRequest]) {
  override def equals(other:Any) = other match {
    case that:Column => that.title == title
    case _ => false
  }
}

object Column {
  implicit val reader: Reads[Column] = (
    (__ \ "title").read[String] and
    (__ \ "unifiedRequests").read[Seq[UnifiedRequest]]
  )(Column.apply _)
        
  implicit val writes = new Writes[Column] {
    def writes(c: Column): JsValue = {
      Json.obj(
        "title" -> c.title,
        "unifiedRequests" -> Json.toJson(c.unifiedRequests)
      )
    }
  }
  
  def toBSON(column:Column) = {
    val unifiedRequests = BSONArray().toAppendable
        for (unifiedRequest <- column.unifiedRequests) yield {
          val args = BSONDocument().toAppendable
          for ((argKey, argValue) <- unifiedRequest.args.getOrElse(Map.empty)) yield {
            args.append(argKey -> BSONString(argValue))
          }
          unifiedRequests.append(BSONDocument(
            "service" -> BSONString(unifiedRequest.service),
            "args" -> args))
        }
        BSONDocument(
            "title" -> BSONString(column.title),
            "unifiedRequests" -> unifiedRequests)
  }
}