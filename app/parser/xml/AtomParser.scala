package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.NoAuth

object AtomParser extends GenericXmlParser {

  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "entry")
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    Some(Skimbo(
        (node \ "author" \ "name").text,
        (node \ "author" \ "name").text,
        (node \ "title").text,
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