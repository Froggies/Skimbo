package services.auth

import services.auth.actions._
import services.UserDao
import play.api.mvc._
import models.user.SkimboToken
import services.commands.CmdFromUser
import services.actors.UserInfosActor
import models.command.NewToken

trait AuthProvider extends GenericProvider with AccountWsProvider with SecurityProvider {

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","
    
  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)
  
  override def deleteToken(implicit request: RequestHeader) = {
    UserDao.removeToken(request.session("id"), this)
  }
  
  protected def startUser(token:SkimboToken, redirectRoute: Call)(implicit request: RequestHeader) = {
    val session = generateUniqueId(request.session)
    UserDao.setToken(session("id"), this, token)
    CmdFromUser.interpretCmd(session("id"), NewToken.asCommand(this))
    UserInfosActor.refreshInfosUser(session("id"), this)
    UserInfosActor.restartProviderColumns(session("id"), this)
    Redirect(redirectRoute).withSession(session)
  }
  
}