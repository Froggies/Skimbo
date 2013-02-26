package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.json.GenericJsonParser
import models.Skimbo
import services.auth.providers.Trello

case class TrelloWallMessage(
  id: String,
  unread: Boolean,
  trelloType: String,
  date: DateTime,
  data: DataTrello,
  idMemberCreator: String,
  memberCreator: MemberCreatorTrello
)

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

object TrelloWallParser extends GenericJsonParser {

  val avatarUrl = "https://trello-avatars.s3.amazonaws.com/%s/30.png"
  val urlBoard = "https://trello.com/board/%s"
  val urlCard = "https://trello.com/card/%s/%s"

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[TrelloWallMessage](json).fold(
      error => logParseError(json, error, "ViadeoWallMessage"),
      e => Some(Skimbo(
        e.memberCreator.fullName,
        e.memberCreator.username,
        generateText(e),
        e.date,
        Nil,
        -1,
        generateLink(e),
        e.id,
        generateAvatarUrl(e),
        Trello)))
  }

  def generateText(e: TrelloWallMessage) = {
    val sb = new StringBuilder
    if (e.data.board.isDefined) {
      sb append "[" append e.data.board.get.name append "] "
    }
    if (e.data.card.isDefined) {
      sb append e.data.card.get.name + " - "
    }
    if (e.data.text.isDefined) {
      sb append e.data.text.get
    } else {
      sb append e.trelloType
    }
    sb.toString()
  }

  def generateLink(e: TrelloWallMessage) = {
    e.data.board.map(board =>
      e.data.card.map(card =>
        urlCard.format(board.id, card.idShort.get))
        .getOrElse(
          urlBoard.format(board.id)))
  }

  def generateAvatarUrl(e: TrelloWallMessage) = {
    e.memberCreator.avatarHash.map(avatar =>
      if (avatar.isEmpty()) {
        ""
      } else {
        avatarUrl.format(avatar)
      })
  }

  override def nextSinceId(sinceId: String, compareSinceId: String): String = {
    if (compareSinceId.isEmpty()) {
      sinceId
    } else {
      if ((sinceId compareTo compareSinceId) > 1) {
        sinceId
      } else {
        compareSinceId
      }
    }
  }

}

object MemberCreatorTrello {
  implicit val jsonReader: Reads[MemberCreatorTrello] = (
    (__ \ "id").read[String] and
    (__ \ "avatarHash").readNullable[String] and
    (__ \ "fullName").read[String] and
    (__ \ "initials").read[String] and
    (__ \ "username").read[String])(MemberCreatorTrello.apply _)
}

object CardTrello {
  implicit val cardReader: Reads[CardTrello] = (
    (__ \ "name").read[String] and
    (__ \ "idShort").readNullable[Int] and
    (__ \ "id").read[String])(CardTrello.apply _)
}

object BoardTrello {
  implicit val boardReader: Reads[BoardTrello] = (
    (__ \ "name").read[String] and
    (__ \ "id").read[String])(BoardTrello.apply _)
}

object DataTrello {
  implicit val dataTrelloReader: Reads[DataTrello] = (
    (__ \ "text").readNullable[String] and
    (__ \ "card").readNullable[CardTrello] and
    (__ \ "board").readNullable[BoardTrello])(DataTrello.apply _)
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