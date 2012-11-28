package services.security

import play.api.mvc._
import models.User

trait Authentication { this: Controller => 

    def Authenticated(action: User => Request[AnyContent] => Result) = Action { implicit request =>
      session.get("id") match {
        case Some(userId) => action(User.create(userId))(request)
        case _ => Redirect(controllers.routes.Application.index)
      }
    }

}