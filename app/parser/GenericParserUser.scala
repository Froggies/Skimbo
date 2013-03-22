package parser

import services.auth.GenericProvider
import play.api.libs.ws.Response
import models.user.ProviderUser;
import play.api.mvc.RequestHeader

trait GenericParserUser {

  def getProviderUser(response: Response, provider: GenericProvider)(implicit request: RequestHeader):Option[List[ProviderUser]]
  
}