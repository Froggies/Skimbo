package parser.json

import play.api.libs.json.JsPath
import play.api.libs.json.Reads
import play.api.libs.json.JsSuccess

object SkimboJsPath {

  /**
   * Like readNullable, but return None if the path is missing
   */
  implicit class RichJsPath(jsonPath: JsPath) extends JsPath {
    def tryReadNullable[T](implicit r: Reads[T]): Reads[Option[T]] = Reads( js =>
      Reads.nullable[T](this)(r).reads(js).orElse(JsSuccess(None))
    )
  }

}