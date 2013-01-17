package services.auth

import play.api.libs.ws._
import play.api.mvc._
import play.api.http._
import play.api.libs.ws.WS.WSRequestHolder

object NoAuth extends GenericProvider {
  override val name = "noauth"
  override val namespace = "noauth"
    
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder =
    WS.url(url)
  
  // TODO : rework architecture because functions bellow aren't required
    
  def auth(redirectRoute: play.api.mvc.Call)(implicit request: play.api.mvc.RequestHeader): play.api.mvc.Result =
    Redirect(controllers.routes.Application.closePopup)
  
  override def hasToken(implicit request: play.api.mvc.RequestHeader) = true

}