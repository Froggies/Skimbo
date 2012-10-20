package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._

object Scoopit extends OAuthProvider {

  override val name = "scoopit"
  override val namespace = "sc"

}