package services.auth

import services.auth.actions._
import services.UserDao
import play.api.mvc._

trait AuthProvider extends GenericProvider with AccountWsProvider with SecurityProvider {

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","
    
  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)
  
  override def deleteToken(implicit request: RequestHeader) = {
    UserDao.removeToken(request.session("id"), this)
  }
  
}