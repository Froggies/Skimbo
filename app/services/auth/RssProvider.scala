package services.auth

import play.api.libs.ws._
import play.api.mvc._
import play.api.libs.ws.WS.WSRequestHolder

object RssProvider extends GenericProvider {
  override val name = "rss"
  override val namespace = "rss"

  def fetch(idUser: String, url: String): WSRequestHolder =
    WS.url(url)

}