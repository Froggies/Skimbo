package models
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsString

case class User(
  id: String,
  username: String,
  name: String,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {
  def fromSession(sess: play.api.mvc.Session): Option[User] = {
    for (
      id <- sess.get("id");
      login <- sess.get("id"); // TODO : Aller chercher les infos en BDD
      social <- sess.get("id");
      name <- sess.get("id");
      desc <- None
    ) yield User(id, login, name, social, desc, None)
  }

  def fromJson(json: JsValue): User = {
    User((json \ "id").as[String], 
        (json \ "login").as[String], 
        (json \ "name").as[String], 
        (json \ "social").as[String], 
        (json \ "desc").asOpt[String], 
        (json \ "avatar").asOpt[String])
  }

  def toJson(user: User): JsValue = {
    JsString("b")
  }

}