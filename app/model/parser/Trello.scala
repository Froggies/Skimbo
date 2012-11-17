package model.parser

import org.joda.time.DateTime
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import model.Skimbo
import services.auth.providers.Trello

case class TrelloWallMessage(
  id: String,
  unread: Boolean,
  trelloType: String,
  date: DateTime,
  data: DataTrello,
  idMemberCreator: String,
  memberCreator: MemberCreatorTrello)

case class DataTrello(
  text: Option[String],
  card: Option[CardTrello],
  board: Option[BoardTrello])

case class CardTrello(
  name: String,
  idShort: Option[Int],
  id: String)

case class BoardTrello(
  name: String,
  id: String)

case class MemberCreatorTrello(
  id: String,
  avatarHash: Option[String],
  fullName: String,
  initials: String,
  username: String)

object TrelloWallParser extends GenericParser[TrelloWallMessage] {

  override def asSkimbos(elements: List[TrelloWallMessage]): List[Skimbo] = {
    for (e <- elements) yield asSkimbo(e).get
  }

  override def asSkimbo(e: TrelloWallMessage): Option[Skimbo] = {
    Some(Skimbo(
      e.memberCreator.fullName,
      e.memberCreator.username,
      generateText(e.trelloType, e.data.text),
      e.date,
      Nil,
      0,
      generateLink(e.id),
      e.date.toString(),
      Trello))
  }

  def generateText(trelloType: String, text: Option[String]) = {
    text.getOrElse(trelloType)
  }

  def generateLink(id: String) = {
    Some(id)
  }

  override def cut(json: JsValue): List[JsValue] = {
    json.as[List[JsValue]]
  }

  //FIXME : found better if you can !!!!!!!
  def transform(json: JsValue): JsValue = {
    Json.toJson(asSkimbo(Json.fromJson[TrelloWallMessage](json).get))
  }

}

object MemberCreatorTrello {
  implicit val jsonReader: Reads[MemberCreatorTrello] = (
    (__ \ "id").read[String] and
    (__ \ "avatarHash").readOpt[String] and
    (__ \ "fullName").read[String] and
    (__ \ "initials").read[String] and
    (__ \ "username").read[String])(MemberCreatorTrello.apply _)
}

object CardTrello {
  implicit val cardReader: Reads[CardTrello] = (
    (__ \ "name").read[String] and
    (__ \ "idShort").readOpt[Int] and
    (__ \ "id").read[String])(CardTrello.apply _)
}

object BoardTrello {
  implicit val boardReader: Reads[BoardTrello] = (
    (__ \ "name").read[String] and
    (__ \ "id").read[String])(BoardTrello.apply _)
}

object DataTrello {
  implicit val dataTrelloReader: Reads[DataTrello] = (
    (__ \ "text").readOpt[String] and
    (__ \ "card").readOpt[CardTrello] and
    (__ \ "board").readOpt[BoardTrello])(DataTrello.apply _)
}

object TrelloWallMessage {
  implicit val trelloReader: Reads[TrelloWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "unread").read[Boolean] and
    (__ \ "type").read[String] and
    (__ \ "date").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")) and
    (__ \ "data").read[DataTrello] and
    (__ \ "idMemberCreator").read[String] and
    (__ \ "memberCreator").read[MemberCreatorTrello])(TrelloWallMessage.apply _)
}