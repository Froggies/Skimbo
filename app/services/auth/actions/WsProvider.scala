package services.auth.actions

import play.api.libs.json.JsValue
import play.api.libs.ws.Response
import play.api.libs.ws.WS.WSRequestHolder
import play.api.mvc.RequestHeader

trait WsProvider {

  /**
   * Create a basic webservice call and sign the request with token
   */
  def fetch(idUser: String, url: String): WSRequestHolder
  
  def resultAsJson(response: Response): JsValue = response.json

  def isInvalidToken(idUser:String, response: play.api.libs.ws.Response): Boolean = 
    response.body.contains("token")

  def isRateLimiteError(response: Response): Boolean = response.body.contains("rate")

}