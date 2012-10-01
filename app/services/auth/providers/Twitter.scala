package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object Twitter extends OAuthProvider {

  override val name = "twitter"
  override val namespace = "tw"

}

