package services.post

import play.api.mvc.RequestHeader
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait GenericPoster extends Poster {

  def post(post:models.Post)(implicit request: RequestHeader) = {
    authProvider.post(urlToPost(post), postParams(post), postHeaderParams, postContent(post)).onComplete {
      case Success(response) => {
        println(response.body.toString)
      }
      case Failure(error) => {
        println(error)
      }
    }
  }
  
}