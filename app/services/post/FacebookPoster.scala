package services.post

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader
import services.auth.providers.Facebook

object FacebookPoster extends GenericPoster {

  override val authProvider = Facebook
  
  override def urlToPost(post:models.Post) = {
    if(post.toPageId.isDefined) {
      "https://graph.facebook.com/"+post.toPageId.get+"/feed"
    } else {
      "https://graph.facebook.com/366546810094718/feed"
    }
  }
  
  override def postParams(post:models.Post)(implicit request: RequestHeader):Seq[(String, String)] = {
    
    val token:Future[Option[String]] = Facebook.fetch("https://graph.facebook.com/me/accounts").get.map { response =>
      val pages = (response.json \ "data").as[List[JsValue]]
      val optToken: List[Option[String]] = pages.map { json =>
        val id = (json \ "id").as[String]
        if(id == "366546810094718") {//}post.toPageId.get) {
          println("FACEBOOK foudn token")
          (json \ "access_token").asOpt[String]
        } else {
          None
        }
      }
      if(!optToken.isEmpty && optToken.head.isDefined) {
        optToken.head
      } else {
        None
      }
    }
    Seq("message" -> post.message) ++ 
    post.url.map(url => Seq("link" -> url)).getOrElse(Seq.empty) ++
    post.url_image.map(url => Seq("picture" -> url)).getOrElse(Seq.empty) ++
    Await.result(token, Duration("5 seconds")).map(t => Seq("access_token" -> t)).getOrElse(Seq.empty)
  }
  
}