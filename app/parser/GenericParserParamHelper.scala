package parser

import play.api.libs.ws.Response
import services.auth.GenericProvider
import play.api.mvc.RequestHeader
import models.ParamHelper

trait GenericParserParamHelper {

    def getParamsHelper
          (response: Response, provider: GenericProvider)
          (implicit request: RequestHeader)
        : Option[List[ParamHelper]]
  
}