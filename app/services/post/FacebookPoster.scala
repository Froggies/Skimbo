package services.post

import play.api.mvc.RequestHeader
import services.auth.providers.Facebook

object FacebookPoster extends GenericPoster {

  override val authProvider = Facebook
  
  override def urlToPost(post:models.Post) = "https://graph.facebook.com/me/feed"
  
  override def postParams(post:models.Post)(implicit request: RequestHeader):Seq[(String, String)] = {
    Seq("message" -> post.message) ++ 
    post.url.map(url => Seq("link" -> url)).getOrElse(Seq.empty) ++
    post.url_image.map(url => Seq("picture" -> url)).getOrElse(Seq.empty)
  }
  
}