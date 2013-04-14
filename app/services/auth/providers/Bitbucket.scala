package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import play.api.mvc.WithHeaders
import models.user.ProviderUser
import play.api.libs.concurrent.futureToPlayPromise
import models.user.SkimboToken
import parser.json.providers.BitbucketUser

object Bitbucket extends OAuthProvider {

  override val name = "bitbucket"
  override val namespace = "bb"

  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    try {
      BitbucketUser.asProviderUser(idUser, response.json)
    } catch {
      case _ : Throwable => {
        Logger.error("Error during fetching user details BITBUCKET")
        None
      }
    }
  }

}