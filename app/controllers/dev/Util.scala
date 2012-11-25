package controllers.dev;

import play.api.mvc._
import services.auth.providers._
import services.endpoints.Endpoints
import scala.concurrent.future
import services.security.AuthenticatedAction.Authenticated
import controllers.UserDao
import services.auth.ProviderDispatcher
import play.api.libs.json.Json

object Util extends Controller {

  def testRes(service: String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      Endpoints.getConfig(service).flatMap { config => 
        Endpoints.genererUrl(service, Map.empty, None).map { url => 
          config.provider.fetch(url).get.map { response =>
            Ok(config.provider.resultAsJson(response))
          }
        }
      }.getOrElse(future(Ok("Service not found")))
    }
  }
  
  def deleteUser() = Authenticated { action => 
    import scala.concurrent.ExecutionContext.Implicits.global
    UserDao.findOneById(action.user.accounts.head.id).map { user => 
      user map { u =>
        UserDao.delete(u)
      }
    }
    Ok("user deleted")
  }
  
  //TODO remove this !!
  def deleteAllUsers() = Authenticated { action => 
    import scala.concurrent.ExecutionContext.Implicits.global
    UserDao.findAll.foreach { users => 
      users foreach { user =>
        UserDao.delete(user)
      }
    }
    Ok("users deleted")
  }
  
  def userDistantRes(providerName: String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
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
