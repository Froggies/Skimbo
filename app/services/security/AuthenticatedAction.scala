package services.security

import play.api.mvc._
import models.User
import controllers.UserDao
import models.Account
import java.util.Date

object AuthenticatedAction extends Results {

  case class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) { request =>
      request.session.get("id").map {id => 
        //just for common use in AuthenticatedAction, not really needed
        f(AuthenticatedRequest(User.create(id), request))
      }.getOrElse(Redirect(controllers.routes.Application.index))
    }
  }

  // Overloaded method to use the default body parser
  import play.api.mvc.BodyParsers._
  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent]  = {
    Authenticated(parse.anyContent)(f)
  }

}