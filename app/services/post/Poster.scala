package services.post

import play.api.libs.ws.WS
import play.api.libs.oauth.OAuthCalculator
import services.auth.OAuthProvider
import models.Post
import play.api.mvc.RequestHeader
import play.api.libs.json.Json
import services.auth.AuthProvider
import models.ParamHelper
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait Poster {

  val authProvider: AuthProvider

  lazy val name: String = authProvider.name

  val canHavePageId: Boolean = false

  def hasToken(idUser: String): Boolean = authProvider.hasToken(idUser)

  def urlToPost(post: Post): String = ""

  def postHeaderParams(): Seq[(String, String)] = Seq.empty

  def postParams(idUser: String, post: Post): Seq[(String, String)] = Seq.empty

  def postContent(post: Post): String = ""

  def post(idUser: String, post: Post)

  def helperPageId(idUser: String, search: String): Future[Seq[ParamHelper]] = Future(Seq.empty)

}

object Posters {

  val posters: Seq[Poster] = Seq(
    TwitterPoster,
    FacebookPoster,
    FacebookPagePoster,
    LinkedinPoster,
    GithubPoster,
    GoogleplusPoster,
    ScoopitPoster,
    ViadeoPoster)

  def getPoster(name: String): Option[Poster] = {
    posters.find(_.name == name)
  }

  def toJson(idUser: String) = {
    val services = posters.map { service =>
      Json.obj(
        "service" -> service.name,
        "hasToken" -> service.hasToken(idUser),
        "canHavePageId" -> service.canHavePageId,
        "tokenProvider" -> service.authProvider.name)
    }
    Json.toJson(services)
  }

}