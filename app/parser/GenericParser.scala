package parser

import models.Skimbo
import play.api.libs.ws.Response
import services.auth.GenericProvider

trait GenericParser {
  
  def getSkimboMsg(response:Response, provider: GenericProvider): Option[List[Skimbo]]
  
  def nextSinceId(sinceId:String, sinceId2:String): String = sinceId 
  
}