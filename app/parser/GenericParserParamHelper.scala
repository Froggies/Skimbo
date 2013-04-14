package parser

import play.api.libs.ws.Response
import services.auth.GenericProvider
import play.api.mvc.RequestHeader
import models.ParamHelper

trait GenericParserParamHelper {

    def getParamsHelper(idUser: String, response: Response, provider: GenericProvider): Option[List[ParamHelper]]
  
}