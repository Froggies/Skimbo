package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.NoAuth

object RssParser extends GenericXmlParser {

  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "channel" \ "item")
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    Some(Skimbo(
        (node \ "author" \ "title").text,
        (node \ "author" \ "title").text,
        (node \ "description").text,
        new DateTime(),
        List(),
        0,
        None,
        "",
        None,
        NoAuth
    ))
  }
}