package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.RssProvider
import org.joda.time.format.DateTimeFormat
import java.util.Locale

object RssParser extends GenericXmlParser {

  val dateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM YYYY HH:mm:ss Z").withLocale(Locale.US);
  
  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "channel" \ "item")
  }
  
  def asSkimbo(node: scala.xml.Node, global: scala.xml.Elem) : Option[Skimbo] = {
    val asAuthor = node.child exists ( _.label == "author" )
    val author = 
      if(asAuthor) {
        val a = node.child find ( _.label == "author")
        a.map( _.text ).getOrElse((node \ "title").text)
      } else {
        (node \ "title").text
      }
    println("-->"+ asAuthor + " :: " + author)
    Some(Skimbo(
        author,
        author,
        (node \ "description").text,
        DateTime.parse((node \ "pubDate").text, dateTimeFormatter),
        List(),
        0,
        Some((node \ "link").text),
        "",
        None,
        RssProvider
    ))
  }
}