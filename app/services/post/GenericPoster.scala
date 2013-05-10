package services.post

import play.api.mvc.RequestHeader
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import play.api.libs.ws.Response

trait GenericPoster extends Poster {

  def post(idUser: String, post:models.Post): Future[Response] = {
    authProvider.post(idUser, urlToPost(post), postParams(idUser, post), postHeaderParams, postContent(post))
  }
  
}