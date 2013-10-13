package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.RssProvider
import org.joda.time.format.DateTimeFormat
import services.endpoints.Configuration

object AtomParser extends GenericXmlParser {

  val dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  val dateTimeFormatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
  
  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "entry")
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    val date = foundDateTime(node)
    Some(Skimbo(
        date.toDate().getTime().toString,
        (node \ "author" \ "name").text,
        (node \ "author" \ "name").text,
        (node \ "title").text,
        date,
        List(),
        -1,
        Some((node \ "link" \ "@href").text),
        foundDateTime(node).getMillis().toString,
        RssHelper.foundImg(node),
        Configuration.Rss.rss
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