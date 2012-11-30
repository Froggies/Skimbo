package xml

import services.auth.GenericProvider
import scala.xml.NodeSeq
import play.api.Logger
import json.Skimbo

trait GenericXmlParser {
  
  def cutSafe(response: play.api.libs.ws.Response, provider: GenericProvider): Option[NodeSeq] = {
    try {
      Some(cut(response.xml))
    } catch {
      
      case err: Throwable => {
        Logger.error("Unexpected message", err)
        Logger.info(response.body)
        None
      }
    }
  }
  
  def cut(xml: scala.xml.Elem) : NodeSeq
  def toSkimbo(node: scala.xml.Node) : Skimbo

}