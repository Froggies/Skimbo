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

object TrelloWallParser extends GenericParser {

  val avatarUrl = "https://trello-avatars.s3.amazonaws.com/%s/30.png"
    val urlBoard = "https://trello.com/board/%s"
    val urlCard = "https://trello.com/card/%s/%s"
  
  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val e = Json.fromJson[TrelloWallMessage](json).get
    Some(Skimbo(
      e.memberCreator.fullName,
      e.memberCreator.username,
      generateText(e),
      e.date,
      Nil,
      0,
      generateLink(e),
      e.date.toString(),
      generateAvatarUrl(e),
      Trello))
  }

  def generateText(e: TrelloWallMessage) = {
    val sb = new StringBuilder
    if(e.data.board.isDefined) {
      sb append "[" append e.data.board.get.name append "] "
    }
    if(e.data.card.isDefined) {
      sb append e.data.card.get.name + " - "
    }
    if(e.data.text.isDefined) {
      sb append e.data.text.get
    } else {
      sb append e.trelloType
    }
    sb.toString()
  }

  def generateLink(e: TrelloWallMessage) = {
    if(e.data.card.isDefined) {
      Some(urlCard.format(e.data.board.get.id, e.data.card.get.idShort.get))
    } else {
      Some(urlBoard.format(e.data.board.get.id))
    }
  }
  
  def generateAvatarUrl(e:TrelloWallMessage) = {
    if(e.memberCreator.avatarHash.isDefined) {
      Some(avatarUrl.format(e.memberCreator.avatarHash.get))
    } else {
      None
    }
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