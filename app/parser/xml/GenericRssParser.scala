package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import play.api.Logger

object GenericRssParser extends GenericXmlParser {

  def cut(xml: scala.xml.Elem) : NodeSeq = {
    val isAtom = xml.child exists ( _.label == "entry" )
    if(isAtom) {
      AtomParser.cut(xml)
    } else {
      RssParser.cut(xml)
    }
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    val isAtom = node exists ( _.label == "entry" )
    if(isAtom) {
      AtomParser.asSkimbo(node)
    } else {
      RssParser.asSkimbo(node)
    }
  }
  
}