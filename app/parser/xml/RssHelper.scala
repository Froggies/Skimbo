package parser.xml

object RssHelper {

  def foundImg(node: scala.xml.Node):Option[String] = {
    val imgPattern = """(https?:\/\/.*\.(?:png|jpg))""".r
    imgPattern findFirstIn node.text
  }
  
}