package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.RssProvider
import org.joda.time.format.DateTimeFormat

object AtomParser extends GenericXmlParser {

  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  val dateTimeFormatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
  
  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "entry")
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    println(new DateTime().toString(dateTimeFormatter2))
    Some(Skimbo(
        (node \ "author" \ "name").text,
        (node \ "author" \ "name").text,
        (node \ "title").text,
        foundDateTime(node),
        List(),
        -1,
        Some((node \ "link" \ "@href").text),
        foundDateTime(node).toDate().getTime().toString,
        RssHelper.foundImg(node),
        RssProvider
    ))
  }
  
  def foundDateTime(node :scala.xml.Node) = {
    val dateText = (node \ "updated").text
    try {
      DateTime.parse(dateText, dateTimeFormatter)
    } catch {
      case _:Throwable => try {
        DateTime.parse(dateText, dateTimeFormatter2)
      } catch {
        case _:Throwable => new DateTime()
      }
    }
  }
  
}