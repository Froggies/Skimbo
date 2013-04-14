package parser

import services.auth.GenericProvider
import play.api.libs.ws.Response
import models.user.ProviderUser;
import play.api.mvc.RequestHeader

trait GenericParserUser {

  def getProviderUser(idUser: String, response: Response, provider: GenericProvider): Option[List[ProviderUser]]

}