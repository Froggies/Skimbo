package controllers.dev;

import play.api.mvc._
import services.auth.providers._
import services.endpoints.Endpoints
import scala.concurrent.future
import services.UserDao
import services.auth.ProviderDispatcher
import play.api.libs.json.Json
import services.security.Authentication
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
      
object Util extends Controller with Authentication {

  def testRes(service: String) = Action { implicit request =>
    Async {
      Endpoints.getConfig(service).flatMap { config =>
        Endpoints.genererUrl(service, Map.empty, None).map { url =>
          config.provider.fetch(url).get.map { response =>
            Ok(config.provider.resultAsJson(response))
          }
        }
      }.getOrElse(future(BadRequest("Service not found")))
    }
  }
  
  def staticRes() = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      Endpoints.getConfig("facebook.message").flatMap { config =>
        Endpoints.genererUrl("facebook.message", Map.empty, None).map { url =>
          config.provider.fetch(url).get.map { response =>
            Ok(config.provider.resultAsJson(response))
          }
        }
      }.getOrElse(future(BadRequest("Service not found")))
    }
  }

  def deleteUser() = Authenticated { user =>
    request =>
      Async {
        UserDao.findOneById(user.accounts.head.id).map { user =>
          user.map { u =>
            UserDao.delete(u)
            Ok("user deleted")
          }.getOrElse(BadRequest("Service not found"))
        }
      }
  }

  def deleteAllUsers(pwd: String) = Authenticated { user =>
    request =>
      if (pwd == current.configuration.getString("pwdDelAllDb").get) {
        UserDao.findAll.foreach { users =>
          users foreach { user =>
            UserDao.delete(user)
          }
        }
      }
      Ok("users deleted")
  }

  def userDistantRes(providerName: String) = Action { implicit request =>
    Async {
      ProviderDispatcher(providerName).map { provider =>
        provider.fetch(provider.getUserInfosUrl.get).get.map { response =>
          Ok(response.json)
        }
      }.getOrElse(future(Ok("Service not found")))
    }
  }

  def userRes(providerName: String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      ProviderDispatcher(providerName).map { provider =>
        provider.getUser.map { response =>
          Ok(Json.toJson(response))
        }
      }.getOrElse(future(Ok("Provider not found")))
    }
  }
  
}
