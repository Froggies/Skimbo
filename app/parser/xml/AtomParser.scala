package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.RssProvider
import org.joda.time.format.DateTimeFormat

object AtomParser extends GenericXmlParser {

  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  
  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "entry")
  }
  
  def asSkimbo(node: scala.xml.Node, global: scala.xml.Elem) : Option[Skimbo] = {
    Some(Skimbo(
        (node \ "author" \ "name").text,
        (node \ "author" \ "name").text,
        (node \ "title").text,
        DateTime.parse((node \ "updated").text, dateTimeFormatter),
        List(),
        -1,
        Some((node \ "link" \ "@href").text),
        DateTime.parse((node \ "updated").text, dateTimeFormatter).toDate().getTime().toString,
        Some("assets/img/brand/rss.png"),
        RssProvider
    ))
  }
}