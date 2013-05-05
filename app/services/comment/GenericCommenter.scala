package services.comment

import play.api.mvc.RequestHeader
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait GenericCommenter extends Commenter {

  def comment(idUser: String, comment:models.Comment) = {
    val url = urlToComment(comment)
    val params = commentParams(idUser, comment)
    val headers = commentHeaderParams
    val content = commentContent(comment)
    println("GENERICCOMMENTER url ==> "+url)
    println("GENERICCOMMENTER params ==> "+params)
    println("GENERICCOMMENTER headers ==> "+headers)
    println("GENERICCOMMENTER content ==> "+content)
    authProvider.post(idUser, url, params, headers, content).onComplete {
      case Success(response) => {
        println(response.body.toString)
      }
      case Failure(error) => {
        println(error)
      }
    }
  }
  
}