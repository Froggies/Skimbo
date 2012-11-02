package models
import play.api.libs.json.JsValue
import controllers.UserDao
import services.actors.UserInfosActor
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import scala.collection.Map
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import play.api.libs.json.JsNull

case class User(
  id: String,
  distants: Option[Seq[ProviderUser]] = None)

case class ProviderUser(
  id: String,
  username: String,
  name: String,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {
  def load(id: String): Option[User] = {
    Some(User(id))
  }

  def fromJson(json: JsValue): Option[User] = {
    val id: String = (json \ "id").as[String]
    val distants: Option[Seq[JsValue]] = (json \ "distants").asOpt[Seq[JsValue]]
    distants.flatMap { d =>
      val transform = for (distant <- d) yield {
        ProviderUser((distant \ "id").as[String],
          (distant \ "login").as[String],
          (distant \ "name").as[String],
          (distant \ "social").as[String],
          (distant \ "desc").asOpt[String],
          (distant \ "avatar").asOpt[String])
      }
      Some(User(id, Some(transform)))
    }
  }

  def toJson(user: User): JsValue = {
    val distants = user.distants.map { d => d }.getOrElse {Seq()}
    JsObject(Seq(
      "id" -> JsString(user.id)
      //,"distants" -> JsArray(toJson(distants))
    ))
  }
  
  def toJson(distants: Seq[ProviderUser]): Seq[JsObject] = {
    distants.map { distant =>
     JsObject(
       Seq(
         "id" -> JsString(distant.id),
         "login" -> JsString(distant.username),
         "name" -> JsString(distant.name),
         "social" -> JsString(distant.socialType),
         "desc" -> JsString(distant.description.getOrElse("")),
         "avatar" -> JsString(distant.avatar.getOrElse(""))
       )
     )
    }
  }

}