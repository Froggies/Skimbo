package services.post

import services.auth.providers.Viadeo
import play.api.mvc.RequestHeader
import java.net.URLEncoder

object ViadeoPoster extends GenericPoster {

  override val authProvider = Viadeo
  
  override def urlToPost(post:models.Post) = "https://api.viadeo.com/status"
  
  override def postParams(idUser: String, post:models.Post):Seq[(String, String)] = Seq("message" -> post.message)

}