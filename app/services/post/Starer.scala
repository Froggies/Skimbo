package services.post

import play.api.mvc.RequestHeader
import play.api.libs.ws.Response
import scala.concurrent.Future

trait Starer {

  def urlToStar(idProvider: String): String = ""
  
  def name: String
  
  def star(idProvider: String)(implicit request: RequestHeader):Future[Response]
  
}