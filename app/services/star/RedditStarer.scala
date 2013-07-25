package services.star

import services.auth.providers.Reddit
import scala.concurrent.Future
import play.api.libs.ws.Response
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.Logger

object RedditStarer extends GenericStarer {

  override val authProvider = Reddit

  override def star(idUser: String, idProvider: String):Future[Response] = {
    Reddit.fetch(idUser, "https://oauth.reddit.com/api/info.json").get.flatMap { response =>
      val modhash = (response.json \ "data" \ "modhash").as[String]
      Logger.info("modhash "+modhash);
      Logger.info("id "+idProvider);
      val url = "https://oauth.reddit.com/api/vote"
      val params = starParams(idUser, idProvider)
      val headers = starHeaderParams
      val content = Map("dir" -> Seq("1"), "id" -> Seq(idProvider), "uh" -> Seq(modhash))
      authProvider.post(idUser, url, params, headers, content)
    }
  }
  
}