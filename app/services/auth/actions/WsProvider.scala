package services.auth.actions

import play.api.libs.ws.WS.WSRequestHolder
import play.api.mvc.RequestHeader

trait WsProvider {

   /**
   * Create a basic webservice call and sign the request with token
   */
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder

}