package parser.xml

import scala.xml.NodeSeq
import models.Skimbo
import org.joda.time.DateTime
import services.auth.RssProvider
import org.joda.time.format.DateTimeFormat
import java.util.Locale
import services.endpoints.Configuration

object RssParser extends GenericXmlParser {

  val dateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM YYYY HH:mm:ss Z").withLocale(Locale.US)
  val dateTimeFormatter2 = DateTimeFormat.forPattern("EEE, dd MMM YYYY HH:mm:ss 'GMT'").withLocale(Locale.US)
  val dateTimeFormatter3 = DateTimeFormat.forPattern("YYYY-mm-dd'T'HH:mm:ss'Z'")
  
  def cut(xml: scala.xml.Elem) : NodeSeq = {
    (xml \ "channel" \ "item")
  }
  
  def asSkimbo(node: scala.xml.Node) : Option[Skimbo] = {
    val date = foundDateTime(node)
    Some(Skimbo(
        date.toDate().getTime().toString,
        foundAuthor(node),
        foundAuthor(node),
        foundText(node),
        date,
        List(),
        -1,
        Some((node \ "link").text),
        date.getMillis().toString,
        RssHelper.foundImg(node),
        Configuration.Rss.rss
    ))
  }
  
  def foundText(node: scala.xml.Node) = {
    val default = (node \ "description").text
    if(default.isEmpty) {
      (node \ "encoded").text
    } else {
      default
    }
  }
  
  def foundAuthor(node: scala.xml.Node) = {
    val asAuthor = node.child exists ( _.label == "author" )
    if(asAuthor) {
      val a = node.child find ( _.label == "author")
      //println("-->"+a + " :: "+a.get.namespace)
      if(a.isDefined && a.get.namespace == "http://posterous.com/help/rss/1.0") {
        (a.get \ "displayName").text
      } else {
        a.map( _.text ).getOrElse((node \ "title").text)
      }
    } else {
      (node \ "title").text
    }
  }
  
  def foundDateTime(node :scala.xml.Node) = {
    val dateText = 
      if((node \ "pubDate").text isEmpty) {
        (node \ "date").text
      } else {
        (node \ "pubDate").text
      }
    try {
      DateTime.parse(dateText, dateTimeFormatter)
    } catch {
      case _:Throwable => try {
        DateTime.parse(dateText, dateTimeFormatter2)
      } catch {
        case _:Throwable => try {
          DateTime.parse(dateText, dateTimeFormatter3)
        } catch {
          case _:Throwable => new DateTime()
        }
      }
    }
  }
  
}