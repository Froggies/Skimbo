package model.parser

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import model.Skimbo
import services.auth.providers.LinkedIn

case class LinkedInWallMessage(
  numLikes: Option[Int],
  timestamp: DateTime,
  updateType: String,
  updateKey: String,
  person: Option[PersonLinkedIn],
  companyName: Option[String],
  companyPerson: Option[CompanyPersonLinkedIn])

case class PersonLinkedIn(
  currentStatus: Option[String],
  firstName: String,
  headline: Option[String],
  id: String,
  lastName: String,
  pictureUrl: Option[String],
  directLink: Option[String])

case class CompanyPersonLinkedIn(
  code: String,
  person: PersonLinkedIn)

object LinkedInWallParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val e = Json.fromJson[LinkedInWallMessage](json).get
    Some(Skimbo(
      e.person.getOrElse(e.companyPerson.get.person).firstName,
      e.person.getOrElse(e.companyPerson.get.person).lastName,
      generateText(e.updateType, e),
      e.timestamp,
      Nil,
      e.numLikes.getOrElse(-1),
      e.person.getOrElse(e.companyPerson.get.person).directLink,
      e.timestamp.toString(),
      e.person.getOrElse(e.companyPerson.get.person).pictureUrl,
      LinkedIn))
  }

  def generateText(typeLinkedIn: String, e: LinkedInWallMessage) = {
    typeLinkedIn match {
      case "STAT" => {
        val p = e.person.get
        p.firstName + " " + p.lastName + " : " + p.currentStatus.getOrElse("")
      }
      case "CONN" => {
        val p = e.person.get
        "Connexion : " + p.firstName + " " + p.lastName
      }
      case "NCON" => {
        val p = e.person.get
        "New connexion : " + p.firstName + " " + p.lastName
      }
      case "MSFC" => {
        val p = e.companyPerson.get.person
        p.firstName + " " + p.lastName + " follow " + e.companyName.get
      }
      case "JGRP" => {
        val p = e.person.get
        p.firstName + " " + p.lastName + " join group : TODO ADD NAME GROUP"
      }
      case "PICU" => {
        val p = e.person.get
        "New avatar : " + p.firstName + " " + p.lastName
      }
      case "PROF" => {
        val p = e.person.get
        "Modif profil : " + p.firstName + " " + p.lastName
      }
      case _ => "Type msg not parsed"
    }
  }

  override def cut(json: JsValue): List[JsValue] = {
    (json \ "values").as[List[JsValue]]
  }

}

object PersonLinkedIn {
  implicit val linkedInReader: Reads[PersonLinkedIn] = (
    (__ \ "currentStatus").readOpt[String] and
    (__ \ "firstName").read[String] and
    (__ \ "headline").readOpt[String] and
    (__ \ "id").read[String] and
    (__ \ "lastName").read[String] and
    (__ \ "pictureUrl").readOpt[String] and
    (__ \ "siteStandardProfileRequest" \ "url").readOpt[String])(PersonLinkedIn.apply _)
}

object CompanyPersonLinkedIn {
  implicit val linkedInReader: Reads[CompanyPersonLinkedIn] = (
    (__ \ "action" \ "code").read[String] and
    (__ \ "person").read[PersonLinkedIn])(CompanyPersonLinkedIn.apply _)
}

object LinkedInWallMessage {
  implicit val linkedInReader: Reads[LinkedInWallMessage] = (
    (__ \ "numLikes").readOpt[Int] and
    (__ \ "timestamp").read[DateTime] and
    (__ \ "updateType").read[String] and
    (__ \ "updateKey").read[String] and
    (__ \ "updateContent" \ "person").readOpt[PersonLinkedIn] and
    (__ \ "updateContent" \ "company" \ "name").readOpt[String] and
    (__ \ "updateContent" \ "companyPersonUpdate").readOpt[CompanyPersonLinkedIn])(LinkedInWallMessage.apply _)
}