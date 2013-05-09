package daoUser

import scala.Option.option2Iterable

import org.specs2.mutable.Specification

import models.user.Column
import play.api.libs.json.Json

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