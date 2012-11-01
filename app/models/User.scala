package models

case class User(
  id : String,
  username: String,
  name: String,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {
  def apply(sess: play.api.mvc.Session): Option[User] = {
    for (
      id <- sess.get("id");
      login <- sess.get("id"); // TODO : Aller chercher les infos en BDD
      social <- sess.get("id");
      name <- sess.get("id");
      desc <- None
    ) yield User(id, login, name, social, desc, None)
  }
}