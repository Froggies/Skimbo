package models

import scala.Option.option2Iterable

import org.specs2.mutable.Specification

import models.user.Column
import play.api.libs.json.Json

class JsonUserTest extends Specification {

  "JsonUserTest" should {
    "Deserialize json to user" in {
      val jsonMsg = ColumnFixture.jsonMsg
      val columnopt = Json.fromJson[Column](Json.parse(jsonMsg))
      println(columnopt)
      val column = columnopt.get
      column.title must beEqualTo("title2")
      column.unifiedRequests.size must beEqualTo(2)
      column.unifiedRequests(1).args.size must beEqualTo(1)
    }
  }
}