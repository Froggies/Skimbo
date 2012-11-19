package services.auth.actions
import java.io.BufferedReader
import play.api.libs.json.JsValue
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import play.api.libs.json.Json

trait WSGzipJson {

  // TODO > Est surement natif à PLAY2 => Creuser
  // Sinon tenter via un iteratees
  def parseGzipJson(response: play.api.libs.ws.Response): JsValue = {
    def read(buf: BufferedReader, acc: List[String]): List[String] =
      buf.readLine match {
        case null => acc
        case s    => read(buf, s :: acc)
      }

    val buffered = new BufferedReader(new InputStreamReader(new GZIPInputStream(response.ahcResponse.getResponseBodyAsStream()), "UTF-8"))
    val content = read(buffered, Nil).reverse
    Json.parse(content.mkString)
  }
  
}