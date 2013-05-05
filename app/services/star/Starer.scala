package services.star

import play.api.libs.ws.Response
import scala.concurrent.Future
import services.auth.AuthProvider

trait Starer {

  val authProvider: AuthProvider

  lazy val name: String = authProvider.name
  
  def urlToStar(idProvider: String): String = ""
    
  def starHeaderParams(): Seq[(String, String)] = Seq.empty

  def starParams(idUser: String, idProvider: String): Seq[(String, String)] = Seq.empty

  def starContent(idProvider: String): String = ""
  
  def star(idUser: String, idProvider: String):Future[Response]
  
}