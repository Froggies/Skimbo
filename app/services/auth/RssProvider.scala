package services.auth

import play.api.libs.ws._
import play.api.mvc._
import play.api.libs.ws.WS.WSRequestHolder

object RssProvider extends NoAuth {
  override val name = "rss"
  override val namespace = "rss"
    
  def fetch(url: String)(implicit request: RequestHeader): WSRequestHolder =
    WS.url(url)
    
}