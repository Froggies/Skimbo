package models.command

import models.Post
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.bson.DefaultBSONHandlers.BSONLongHandler
import reactivemongo.bson.DefaultBSONHandlers.BSONStringHandler
import reactivemongo.bson.DefaultBSONHandlers.bsonArrayToCollectionReader

case class PostDelayedProvider(
    providerName: String,
    providerPageId: Option[String]
)

object PostDelayedProvider {
  implicit object PostDelayedProviderBSONReader extends BSONDocumentReader[PostDelayedProvider] {
    def read(document: BSONDocument): PostDelayedProvider = {
      PostDelayedProvider(
        document.getAs[String]("providerName").get,
        document.getAs[String]("providerPageId")
      )
    }
  }
  implicit object PostDelayedProviderBSONWriter extends BSONDocumentWriter[PostDelayedProvider] {
    def write(provider: PostDelayedProvider): BSONDocument = {
      BSONDocument(
        "providerName" -> provider.providerName,
        "providerPageId" -> provider.providerPageId
      )
    }
  }
}

case class DelayedPost(
    idUser: String,
    post: Post,
    providers: Seq[PostDelayedProvider],
    timeToPost: Long
)

object DelayedPost {
  implicit object DelayedPostBSONReader extends BSONDocumentReader[DelayedPost] {
    def read(document: BSONDocument): DelayedPost = {
      DelayedPost(
        document.getAs[String]("idUser").get,
        document.getAs[Post]("post").get,
        document.getAs[Seq[PostDelayedProvider]]("providers").get,
        document.getAs[Long]("timeToPost").get
      )
    }
  }
  implicit object DelayedPostBSONWriter extends BSONDocumentWriter[DelayedPost] {
    def write(post: DelayedPost): BSONDocument = {
      BSONDocument(
        "idUser" -> post.idUser,
        "post" -> post.post,
        "providers" -> post.providers,
        "timeToPost" -> post.timeToPost
      )
    }
  }
}