package services.auth.actions

import play.api.libs.ws.WS.WSRequestHolder
import play.api.mvc.RequestHeader
import play.api.libs.ws.Response
import play.api.libs.json.JsValue

trait WsProvider {

   /**
   * Create a basic webservice call and sign the request with token
   */
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder

  def resultAsJson(response: Response): JsValue = response.json
  
  def isInvalidToken(response: Response): Boolean =  response.body.contains("token")
  
}