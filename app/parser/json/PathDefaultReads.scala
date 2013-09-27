package parser.json

import play.api.libs.json.PathReads
import play.api.libs.json.JsPath
import play.api.libs.json.Reads
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsNull

object PathDefaultReads {

  def default[A](path:JsPath, default:A)(implicit reads: Reads[A]) = Reads[A]{ json => 
    path.applyTillLast(json).fold(
      jserr => JsSuccess(default), 
      jsres => jsres.fold( 
        _ => JsSuccess(default),
        a => a match {
          case JsNull => JsSuccess(default)
          case js => reads.reads(js).repath(path)
        }
      )
    )
  }
  
  def defaults[A](path:JsPath, default:Seq[A])(implicit reads: Reads[Seq[A]]) = Reads[Seq[A]]{ json => 
    path.applyTillLast(json).fold(
      jserr => JsSuccess(default), 
      jsres => jsres.fold( 
        _ => JsSuccess(default),
        a => a match {
          case JsNull => JsSuccess(default)
          case js => reads.reads(js).repath(path)
        }
      )
    )
  }
  
}