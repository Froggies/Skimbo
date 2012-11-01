package models

case class User(
  username: String,
  name: String,
  socialType: String,
  description: Option[String] = None,
  avatar: Option[String] = None)

object User {
  def apply(sess: play.api.mvc.Session): Option[User] = {
    for (
      login <- sess.get("user-username");
      social <- sess.get("provider");
      name <- sess.get("user-name");
      desc <- Some(sess.get("user-description"))
    ) yield User(login, name, social, desc, sess.get("user-avatar"))
  }
}