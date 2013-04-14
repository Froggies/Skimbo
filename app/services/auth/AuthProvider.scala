package services.auth

import services.auth.actions._
import services.dao.UserDao
import play.api.mvc._
import models.user.SkimboToken
import services.commands.CmdFromUser
import services.actors.UserInfosActor
import models.command.NewToken
import scala.concurrent.Future
import scala.util.Right
import scala.util.Left
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait AuthProvider extends GenericProvider with AccountWsProvider with SecurityProvider {

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","

  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)

  override def deleteToken(idUser: String) = {
    UserDao.removeToken(idUser, this)
  }

  protected def startUser(token: SkimboToken, redirectRoute: Call)(implicit request: RequestHeader) = {
    val session = generateUniqueId(request.session)
    UserDao.setToken(session("id"), this, token).map { lastError =>
      CmdFromUser.interpretCmd(session("id"), NewToken.asCommand(this))
      UserInfosActor.refreshInfosUser(session("id"), this)
      UserInfosActor.restartProviderColumns(session("id"), this)
      Redirect(redirectRoute).withSession(session)
    }
  }

  def post(idUser: String, url: String, queryString: Seq[(String, String)],
    headers: Seq[(String, String)], content: String): Future[play.api.libs.ws.Response]

}