package services.post

import services.auth.providers.Viadeo
import play.api.mvc.RequestHeader
import java.net.URLEncoder

object ViadeoPoster extends GenericPoster {

  override val authProvider = Viadeo
  
  override def urlToPost(post:models.Post) = {
    if(post.url.isDefined) {
      "https://api.viadeo.com/recommend"
    } else {
      "https://api.viadeo.com/status"
    }
  }
  
  override def postParams(post:models.Post)(implicit request: RequestHeader):Seq[(String, String)] = {
    if(post.url.isDefined) {
      Seq(
        "url" -> post.url.get,
        "title" -> post.title,
        "message" -> URLEncoder.encode(post.message, "UTF-8"),
        "url_picture" -> post.url_image.getOrElse("")
      )
    } else {
      Seq(
        "message" -> URLEncoder.encode(post.message, "UTF-8")
      )
    }
  }

}