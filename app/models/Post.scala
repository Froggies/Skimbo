package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentWriter

case class Post (
  title:String,
  message:String,
  url:Option[String] = None,
  url_image:Option[String] = None,
  toPageId:Option[String] = None
)

object Post {
  implicit val reader = (
    (__ \ "title").read[String] and
    (__ \ "message").read[String] and
    (__ \ "url").readNullable[String] and
    (__ \ "url_image").readNullable[String] and
    (__ \ "toPageId").readNullable[String])(Post.apply _)
    
  implicit object DelayedPostBSONReader extends BSONDocumentReader[Post] {
    def read(document: BSONDocument): Post = {
      Post(
        document.getAs[String]("title").get,
        document.getAs[String]("message").get,
        document.getAs[String]("url"),
        document.getAs[String]("url_image"),
        document.getAs[String]("toPageId")
      )
    }
  }
  
  implicit object DelayedPostBSONWriter extends BSONDocumentWriter[Post] {
    def write(post: Post): BSONDocument = {
      BSONDocument(
        "title" -> post.title,
        "message" -> post.message,
        "url" -> post.url,
        "url_image" -> post.url_image,
        "toPageId" -> post.toPageId
      )
    }
  }
  
}