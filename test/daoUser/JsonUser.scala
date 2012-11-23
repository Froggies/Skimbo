package daoUser

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import json.parser._
import json.Skimbo
import play.api.libs.json.Json
import models.user.Column

class JsonUser extends Specification {

  "JsonUser" should {
    "Deserialize json to user" in {
      val jsonMsg = """{"title":"title2",
  "unifiedRequests":[
    {"service":"viadeo.wall","args":{}},
    {"service":"github.notifications","args":{"username":"manland","$$hashKey":"048"},"$$hashKey":"02H"}
  ]}"""
      val columnopt = Json.fromJson[Column](Json.parse(jsonMsg))
      println(columnopt)
      val column = columnopt.get
      column.title must beEqualTo("title2")
      column.unifiedRequests.size must beEqualTo(2)
      column.unifiedRequests(1).args.size must beEqualTo(1)
    }
  }
}