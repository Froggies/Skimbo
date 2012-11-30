package xml

import scala.xml.NodeSeq
import json.Skimbo
import org.joda.time.DateTime
import services.auth.providers.NoAuth

object ParserAtom extends GenericXmlParser {

  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "entry")
  }
  
  def toSkimbo(node: scala.xml.Node) : Skimbo = {
    Skimbo(
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
    )
  }
}