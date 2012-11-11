package model

import org.joda.time._
import SocialNetwork._
import scala.util.parsing.json.JSONObject
import play.api.libs.json.JsString
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject

/**
* Common format between social networks
*/
case class Skimbo(
  authorName: String,
  authorScreenName: String,
  message: String,
  createdAt: DateTime,
  comments: List[Skimbo],
  shared: Int,
  directLink: Option[String],
  sinceId: String,
  from: SocialNetwork
)

object Skimbo {//TODO rework with implicit writer
  
  def toJson(skimbos:Seq[Skimbo]):JsArray = {
    JsArray(skimbos.map(toJson(_)))
  }
  
  def toJson(skimbo:Skimbo):JsObject = {
    JsObject(Seq(
      "authorName" -> JsString(skimbo.authorName),
      "authorScreenName" -> JsString(skimbo.authorScreenName),
      "message" -> JsString(skimbo.message),
      "createdAt" -> JsString(skimbo.createdAt.toDate().getTime().toString())
    ))
  }
}

