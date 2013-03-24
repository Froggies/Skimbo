package services.post

import services.auth.providers.GooglePlus
import play.api.Logger
import play.api.libs.json.Json

object GoogleplusPoster extends GenericPoster {

  override val authProvider = GooglePlus

  override def urlToPost(post:models.Post) = "https://www.googleapis.com/plus/v1/people/me/moments/vault"
  
  override def postHeaderParams = Seq(("Content-Type", "application/json; charset=UTF-8"))
    
  override def postContent(post:models.Post):String = {
    val res = Json.obj(
      "type" -> "http://schemas.google.com/AddActivity",
      "target" -> Json.obj(
        "name" -> post.title,
        "id" -> post.title,
        "description" -> post.message,
        "type" -> "http://schemas.google.com/AddActivity",
        "image" -> post.url_image
        //"url" -> post.url
      )
    ).toString
    Logger("GooglePlusPoster").info(res)
    res
  }
  
}