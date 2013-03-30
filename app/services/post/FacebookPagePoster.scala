package services.post

import play.api.mvc.RequestHeader
import scala.concurrent.Future
import services.auth.providers.Facebook
import play.api.libs.json.JsValue
import models.ParamHelper
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import parser.json.providers.FacebookWallParser

object FacebookPagePoster extends GenericPoster {

  override val authProvider = Facebook
  
  override lazy val name = "FacebookPage"
  
  override val canHavePageId = true
  
  override def urlToPost(post:models.Post) = "https://graph.facebook.com/"+post.toPageId.get+"/feed"

  override def postParams(post:models.Post)(implicit request: RequestHeader):Seq[(String, String)] = {
    FacebookPoster.postParams(post) ++ getPageToken(post.toPageId.get)
  }
  
  override def helperPageId(search: String)(implicit request: RequestHeader):Future[Seq[ParamHelper]] = {
    Facebook.fetch("https://graph.facebook.com/me/accounts").get.map { response =>
      val pages = (response.json \ "data").as[List[JsValue]]
      pages.map { page => 
        val ph = ParamHelper(
          (page \ "name").as[String],
          (page \ "id").as[String],
          FacebookWallParser.imageUrl.replace(":id", (page \ "id").as[String]),
          None
        )
        ph
      }
    }
  }
  
  private def getPageToken(pageId: String)(implicit request: RequestHeader):Seq[(String, String)] = {
    println("FACEBOOK PAGE :: getPageTOken")
    val token:Future[Option[String]] = Facebook.fetch("https://graph.facebook.com/me/accounts").get.map { response =>
      println("FACEBOOK PAGE :: response :: "+response.body.toString)
      val pages = (response.json \ "data").as[List[JsValue]]
      val optToken: List[Option[String]] = pages.map { json =>
        println("FACEBOOK PAGE :: json :: "+json)
        val id = (json \ "id").as[String]
        if (id == pageId) {
          println("FACEBOOK found token")
          (json \ "access_token").asOpt[String]
        } else {
          None
        }
      }
      if (!optToken.isEmpty && optToken.head.isDefined) {
        optToken.head
      } else {
        None
      }
    }
    Await.result(token, Duration("5 seconds")).map(t => Seq("access_token" -> t)).getOrElse(Seq.empty)
  }
  
}