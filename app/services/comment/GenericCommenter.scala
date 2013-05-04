package services.comment

import play.api.mvc.RequestHeader
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait GenericCommenter extends Commenter {

  def comment(idUser: String, comment:models.Comment) = {
    authProvider.post(idUser, urlToComment(comment), commentParams(idUser, comment), commentHeaderParams, commentContent(comment)).onComplete {
      case Success(response) => {
        println(response.body.toString)
      }
      case Failure(error) => {
        println(error)
      }
    }
  }
  
}