package services.auth.providers

import play.api._
import play.api.mvc._
import services.auth._
import models.user.ProviderUser
import models.user.SkimboToken
import services.commands.CmdToUser
import parser.json.providers.GithubUser

object GitHub extends OAuth2Provider {

  override val name = "github"
  override val namespace = "gh"
  override val method = Post
  override val accessTokenHeaders = Seq("Accept" -> "application/json")
  override val permissions: Seq[String] = Seq(
    "gist"          // Read/write gist
  )

  override def processToken(response: play.api.libs.ws.Response) =
    Token((response.json \ "access_token").asOpt[String], None)

  override def distantUserToSkimboUser(idUser: String, response: play.api.libs.ws.Response): Option[ProviderUser] = {
    if(isInvalidToken(idUser, response)) {
      CmdToUser.sendTo(idUser, models.command.TokenInvalid(name))
      None
    } else {
      try {
      GithubUser.asProviderUser(idUser, response.json)
      } catch {
        case t:Throwable => {
          Logger("G+Provider").error("Error during fetching user details Github "+t.getMessage())
          Logger("G+Provider").error(response.json.toString)
          None
        }
      }
    }
  }
  
}