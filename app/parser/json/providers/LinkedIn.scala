package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.GenericParser
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.LinkedIn

case class LinkedInWallMessage(
  numLikes: Option[Int],
  timestamp: DateTime,
  updateType: String,
  updateKey: String,
  person: Option[PersonLinkedIn],
  companyName: Option[String],
  companyPerson: Option[CompanyPersonLinkedIn]
)

case class PersonLinkedIn(
  currentStatus: Option[String],
  firstName: String,
  headline: Option[String],
  id: String,
  lastName: String,
  pictureUrl: Option[String],
  directLink: Option[String],
  connections: List[ConnectionLinkedIn])

case class CompanyPersonLinkedIn(
  code: String,
  person: PersonLinkedIn)
  
case class ConnectionLinkedIn(
  firstName: String,
  lastName: String,
  headLine: Option[String]
)

object LinkedInWallParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[LinkedInWallMessage](json).fold(
      error => logParseError(json, error, "ViadeoWallMessage"),
      e => 
        if (mustBeIgnored(e)) 
          None
        else Some(Skimbo(
          getName(e),
          getName(e),
          generateText(e),
          e.timestamp,
          Nil,
          e.numLikes.getOrElse(-1),
          e.person.getOrElse(e.companyPerson.get.person).directLink,
          e.timestamp.toInstant().getMillis().toString(),
          e.person.getOrElse(e.companyPerson.get.person).pictureUrl,
          LinkedIn)))
  }
  
  def getName(msg: LinkedInWallMessage) = {
    msg.person.getOrElse(msg.companyPerson.get.person).firstName+ " " +msg.person.getOrElse(msg.companyPerson.get.person).lastName
  }

  def mustBeIgnored(msg: LinkedInWallMessage) = {
    (msg.person.isEmpty) || 
    (!List("STAT", "CONN", "NCON", "MSFC", "JGRP", "PICU", "PROF").contains(msg.updateType)) ||
    (msg.updateType == "CONN" && !msg.person.get.connections.isEmpty && msg.person.get.connections.head.firstName == "private") || 
    (msg.updateType == "STAT" && msg.person.get.currentStatus.isEmpty)
  }

  def generateText(e: LinkedInWallMessage) = {
    e.updateType match {
      case "STAT" => {
        e.person.get.currentStatus.get
      }
      case "CONN" => {
        e.person.get.connections.headOption.map(friend => 
          "Connection with " + friend.firstName +" "+ friend.lastName + " (" +
          friend.headLine.getOrElse("New connection") + ")")
          .getOrElse("New connection")
      }
      case "NCON" => {
        "New connexion" // TODO
      }
      case "MSFC" => {
        "Follow " + e.companyName.get
      }
      case "JGRP" => {
        val p = e.person.get
        "Join group : TODO ADD NAME GROUP" // TODO
      }
      case "PICU" | "PROF" => {
        "Profile updated"
      }
    }
  }

  override def cut(json: JsValue): List[JsValue] = {
    val count = (json \ "_total").as[Int]
    if(count > 0) {
      super.cut(json \ "values")
    } else {
      List.empty
    }
  }
  
  override def nextSinceId(sinceId: String, compareSinceId: String): String = {
    if (compareSinceId.isEmpty()) {
      sinceId
    } else {
      if (sinceId.toLong > compareSinceId.toLong) {
        sinceId
      } else {
        compareSinceId
      }
    }
  }

}

object PersonLinkedIn {
  implicit val linkedInReader: Reads[PersonLinkedIn] = (
    (__ \ "currentStatus").readNullable[String] and
    (__ \ "firstName").read[String] and
    (__ \ "headline").readNullable[String] and
    (__ \ "id").read[String] and
    (__ \ "lastName").read[String] and
    (__ \ "pictureUrl").readNullable[String] and
    (__ \ "siteStandardProfileRequest" \ "url").readNullable[String] and
    (__ \ "connections" \ "values").readNullable[List[ConnectionLinkedIn]].map(_.getOrElse(List.empty)))(PersonLinkedIn.apply _)
}

object CompanyPersonLinkedIn {
  implicit val linkedInReader: Reads[CompanyPersonLinkedIn] = (
    (__ \ "action" \ "code").read[String] and
    (__ \ "person").read[PersonLinkedIn])(CompanyPersonLinkedIn.apply _)
}

object LinkedInWallMessage {
  implicit val linkedInReader: Reads[LinkedInWallMessage] = (
    (__ \ "numLikes").readNullable[Int] and
    (__ \ "timestamp").read[DateTime] and
    (__ \ "updateType").read[String] and
    (__ \ "updateKey").read[String] and
    (__ \ "updateContent" \ "person").readNullable[PersonLinkedIn] and
    (__ \ "updateContent" \ "company" \ "name").readNullable[String] and
    (__ \ "updateContent" \ "companyPersonUpdate").readNullable[CompanyPersonLinkedIn])(LinkedInWallMessage.apply _)
}

object ConnectionLinkedIn {
  implicit val connectionLinkedInReader: Reads[ConnectionLinkedIn] = (
    (__ \ "firstName").read[String] and
    (__ \ "lastName").read[String] and
    (__ \ "headline").readNullable[String])(ConnectionLinkedIn.apply _) 
}