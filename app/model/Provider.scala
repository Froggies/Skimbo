package model;

import play.api.libs.json._

case class Provider(
	name:String,
	urlAuth:String,
	urlImg:String
)

object Provider {
	def fromJson(json:JsValue):Provider = {
		Provider("", "", "")
	}

	def toJson(provider:Provider):JsValue = {
		JsObject(
      "name" -> JsString(provider.name) ::
      "urlAuth" -> JsString(provider.urlAuth) ::
      "urlImg" -> JsString(provider.urlImg) ::
      Nil
    )
	}
}


