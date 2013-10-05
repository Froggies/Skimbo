package models

import scala.Option.option2Iterable

import org.specs2.mutable.Specification

import models.user.Column
import play.api.libs.json.Json

class JsonUserTest extends Specification {

  "JsonUserTest" should {
    "Deserialize json to user" in {
      val jsonMsg = ColumnFixture.newJsonMsg
      val column = Json.fromJson[Column](Json.parse(jsonMsg)).get
      println(column)
      column.title must beEqualTo("title2")
      column.unifiedRequests.size must beEqualTo(1)
      column.unifiedRequests(0).args.size must beEqualTo(1)
    }
  }
}