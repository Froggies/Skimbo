package services.auth

import play.api.Play.current
import play.api.mvc._
import play.api.libs.ws.WS.WSRequestHolder
import services.auth.actions.WsProvider

trait GenericProvider extends Results with WsProvider {

  // Generic provider settings (override it)
  def name: String
  protected def namespace: String

  // From config file
  protected lazy val config = current.configuration.getConfig("social." + name).get
  
  //TODO remove this because rss provider hasn't token
  //def hasToken(implicit request: play.api.mvc.RequestHeader) = true
  
  def needToken():Boolean = this.isInstanceOf[AuthProvider]
  
  def isAuthProvider:Boolean = this.isInstanceOf[AuthProvider]
  
  def canStart(implicit request: RequestHeader):Boolean = {
    if(needToken) {
      this.asInstanceOf[AuthProvider].hasToken
    } else {
      true
    }
  }
  
  
}