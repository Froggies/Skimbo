package services.star

import scala.concurrent.Future
import play.api.libs.ws.Response

trait GenericStarer extends Starer {

  override def star(idUser: String, idProvider: String):Future[Response] = {
    val url = urlToStar(idProvider)
    val params = starParams(idUser, idProvider)
    val headers = starHeaderParams
    val content = starContent(idProvider)
    authProvider.post(idUser, url, params, headers, content)
  }

}