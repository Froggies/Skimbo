package services.post

import services.auth.providers.Scoopit
import play.api.mvc.RequestHeader
import java.net.URLEncoder
import scala.concurrent.Future
import models.ParamHelper
import play.api.libs.json.JsValue
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object ScoopitPoster extends GenericPoster {

  override val authProvider = Scoopit
  
  override val canHavePageId = true

  override def urlToPost(post:models.Post) = 
    "http://www.scoop.it/api/1/post?action=create&title="+URLEncoder.encode(post.title, "UTF-8")+
    "&url="+URLEncoder.encode(post.url.getOrElse(""), "UTF-8")+
    "&imageUrl="+URLEncoder.encode(post.url_image.getOrElse(""), "UTF-8")+
    "&content="+URLEncoder.encode(post.message, "UTF-8")+
    "&topicId="+URLEncoder.encode(post.toPageId.get, "UTF-8")
    
  override def helperPageId(search: String)(implicit request: RequestHeader):Future[Seq[ParamHelper]] = {
    Scoopit.fetch("http://www.scoop.it/api/1/profile").get.map { response =>
      val pages = (response.json \ "user" \ "curatedTopics").as[List[JsValue]]
      pages.map { page => 
        val ph = ParamHelper(
          (page \ "name").as[String],
          (page \ "id").as[Long].toString,
          (page \ "smallImageUrl").as[String],
          (page \ "description").asOpt[String]
        )
        ph
      }
    }
  }
  
}