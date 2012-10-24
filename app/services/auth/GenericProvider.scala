package services.auth

import providers._

import play.api.mvc._
import play.api.libs.ws.WS.WSRequestHolder

object GenericProvider {

  val providers:Array[GenericProvider] = Array(Twitter, GitHub, Facebook, 
      GooglePlus, LinkedIn, StackExchange, Trello, Viadeo, Scoopit)

  def hasToken(implicit request: RequestHeader):Boolean = {
    for (provider <- providers) {
      if(provider.hasToken) {
        return true
      }
    }
    return false
  }
}

trait GenericProvider extends Results {

  // Generic provider settings (override it)
  def name: String
  def namespace: String

  def permissions: Seq[String] = Seq.empty
  def permissionsSep = ","

  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)

  // Generic operations
  def hasToken(implicit request: RequestHeader) = {
    getToken match {
      case Some(credentials) => true
      case _ => false
    }
  } 

  // Basic operations on providers
  def auth(redirectRoute: Call)(implicit request: RequestHeader): Result
  def getToken(implicit request: RequestHeader): Any
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder

}