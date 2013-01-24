package parser.xml

import parser.GenericParser
import play.api.libs.ws.Response
import services.auth.GenericProvider
import models.Skimbo
import scala.xml.NodeSeq
import play.api.Logger

trait GenericXmlParser extends GenericParser {

  override def getSkimboMsg(response:Response, provider: GenericProvider): Option[List[Skimbo]] = {
//    Logger.info(response.body)
    val explodedMsgs = cutSafe(response, provider)
    if(explodedMsgs.isEmpty) {
      None
    } else {
      val skimboMsgs = explodedMsgs.get.map(xmlMsg => asSkimboSafe(xmlMsg)).flatten
      Some(skimboMsgs.toList)
    }
  }
  
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
  
  protected def asSkimbo(node: scala.xml.Node) : Option[Skimbo]

  def asSkimboSafe(node: scala.xml.Node) : Option[Skimbo] = {
    try {
      asSkimbo(node)
    } catch {
      case ex : Throwable => {
        Logger.error("Error during parsing this message", ex)
        Logger.info(node.toString)
        None
      }
    }
  }
  
}