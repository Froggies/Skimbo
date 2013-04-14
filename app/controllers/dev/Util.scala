package controllers.dev;

import play.api.mvc._
import services.auth.providers._
import services.endpoints.Endpoints
import scala.concurrent.future
import services.dao.UserDao
import services.auth.ProviderDispatcher
import play.api.libs.json.Json
import services.security.Authentication
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current
import models.user.SkimboToken
import views.html.defaultpages.badRequest
import play.api.libs.iteratee.Enumerator
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Await

object Util extends Controller with Authentication {

  def testRes(service: String) = Action { implicit request =>
    val idUser = request.session.get("id").get
    Async {
      Endpoints.getConfig(service).flatMap { config =>
        Endpoints.genererUrl(service, Map.empty, None).map { url =>
          config.provider.fetch(idUser, url).get.map { response =>
            Ok(config.provider.resultAsJson(response))
          }
        }
      }.getOrElse(future(BadRequest("Service not found")))
    }
  }
  
  def testSkimboRes(service: String) = Action { implicit request =>
    val idUser = request.session.get("id").get
    Async {
      Endpoints.getConfig(service).flatMap { config =>
        Endpoints.genererUrl(service, Map.empty, None).map { url =>
          config.provider.fetch(idUser, url).get.map { response =>
            val res = config.parser.get.getSkimboMsg(response, config.provider)
            Ok(res.map(Json.toJson(_)).toString)
          }
        }
      }.getOrElse(future(BadRequest("Service not found")))
    }
  }

  def staticRes() = Action { implicit request =>
    val idUser = request.session.get("id").get
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      Endpoints.getConfig("github.notifications").flatMap { config =>
        Endpoints.genererUrl("github.notifications", Map("username" -> "playframework"), None).map { url =>
          config.provider.fetch(idUser, url).get.map { response =>
            Ok(config.provider.resultAsJson(response))
          }
        }
      }.getOrElse(future(BadRequest("Service not found")))
    }
  }
  
  def urlTest(id:String) = Action { implicit request =>
    val idUser = request.session.get("id").get
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      Twitter.fetch(idUser, "https://api.twitter.com/1.1/statuses/show.json?id="+id).withTimeout(6000).get.map { response =>
        Ok(response.json)
      }
    }
  }

  def deleteUser() = Authenticated { user =>
    request =>
      Async {
        UserDao.findOneById(user.accounts.head.id).map { user =>
          user.map { u =>
            UserDao.delete(u.accounts.head.id)
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
            UserDao.delete(user.accounts.head.id)
          }
        }
      }
      Ok("users deleted")
  }

  def userDistantRes(providerName: String) = Action { implicit request =>
    val idUser = request.session.get("id").get
    Async {
      ProviderDispatcher(providerName).map { provider =>
        provider.fetch(idUser, provider.getUserInfosUrl.get).get.map { response =>
          Ok(response.json)
        }
      }.getOrElse(future(Ok("Service not found")))
    }
  }

  def userRes(providerName: String) = Action { implicit request =>
    val idUser = request.session.get("id").get
    import scala.concurrent.ExecutionContext.Implicits.global
    Async {
      ProviderDispatcher(providerName).map { provider =>
        provider.getUser(idUser).map { response =>
          Ok(Json.toJson(response))
        }
      }.getOrElse(future(Ok("Provider not found")))
    }
  }

  def invalidToken(providerName: String, pwd: String) = Action { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    if (pwd == current.configuration.getString("pwdDelAllDb").get) {
      ProviderDispatcher(providerName).map { provider =>
        UserDao.findAll().map { users =>
          val userWithProvider = users.filter { user =>
            user.distants.map(_.exists { distant =>
              distant.socialType == providerName
            }).getOrElse(false)
          }
          userWithProvider.map { user =>
            UserDao.setToken(user.accounts.head.id, provider, SkimboToken("1"), None);
          }
        }
      }
    }
    Ok("ok")
  }

}
