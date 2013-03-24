package services.post

import services.auth.providers.Scoopit
import play.api.mvc.RequestHeader
import java.net.URLEncoder

object ScoopitPoster extends GenericPoster {

  override val authProvider = Scoopit

  override def urlToPost(post:models.Post) = "http://www.scoop.it/api/1/post"
  
  override def postParams(post:models.Post)(implicit request: RequestHeader):Seq[(String, String)] = {
    Seq(
      "action" -> "create",
      "title" -> URLEncoder.encode(post.title, "UTF-8"),
      "url" -> URLEncoder.encode(post.url.getOrElse(""), "UTF-8"),
      "imageUrl" -> URLEncoder.encode(post.url_image.getOrElse(""), "UTF-8"),
      "content" -> URLEncoder.encode(post.message, "UTF-8")
    )
  }
  
}