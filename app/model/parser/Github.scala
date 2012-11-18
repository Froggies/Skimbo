package model.parser

import org.joda.time.DateTime
import play.api.libs.json.util._
import play.api.libs.json._
import play.api.libs.json.Reads._
import model.Skimbo
import services.auth.providers.GitHub

case class GithubWallMessage(
  id: String,
  actorLogin: String,
  typeGithub: String,
  fork: Option[GithubForkeEvent],
  push: Option[Seq[GithubPushEvent]],
  createdAt: DateTime,
  url: String,
  download: Option[GithubDownloadEvent])

case class GithubForkeEvent(
  originalRepoName: String,
  newRepoName: Option[String])

case class GithubPushEvent(
  message: String,
  name: String)

case class GithubDownloadEvent(
  description: String,
  url: String)

object GithubWallParser extends GenericParser[GithubWallMessage] {

  override def asSkimbo(e: GithubWallMessage): Option[Skimbo] = {
    Some(Skimbo(
      e.actorLogin,
      e.actorLogin,
      buildMsg(e),
      e.createdAt,
      Nil,
      -1,
      Some(e.url),
      e.createdAt.toString(),
      GitHub))
  }

  def buildMsg(e: GithubWallMessage) = {
    e.typeGithub match {
      case "ForkEvent" => e.fork.get.newRepoName.get + " fork of " + e.fork.get.originalRepoName
      case "PushEvent" => e.push.get.head.name + " pushed : " + e.push.get.head.message
      case "DownloadEvent" => "New download (" + e.download.get.description + ")" + e.download.get.url
      case _ => "Undevelopped type : " + e.typeGithub
    }
  }

  override def cut(json: JsValue): List[JsValue] = {
    json.as[List[JsValue]]
  }

  //FIXME : found better if you can !!!!!!!
  def transform(json: JsValue): JsValue = {
    Json.toJson(asSkimbo(Json.fromJson[GithubWallMessage](json).get))
  }

}

object GithubForkeEvent {
  implicit val githubReader: Reads[GithubForkeEvent] = (
    (__ \ "repo" \ "name").read[String] and
    (__ \ "payload" \ "forkee" \ "html_url").readOpt[String])(GithubForkeEvent.apply _)
}

object GithubPushEvent {
  implicit val githubReader: Reads[GithubPushEvent] = (
    (__ \ "message").read[String] and
    (__ \ "author" \ "name").read[String])(GithubPushEvent.apply _)
}

object GithubDownloadEvent {
  implicit val githubReader: Reads[GithubDownloadEvent] = (
    (__ \ "description").read[String] and
    (__ \ "html_url").read[String])(GithubDownloadEvent.apply _)
}

object GithubWallMessage {
  implicit val githubReader: Reads[GithubWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "actor" \ "login").read[String] and
    (__ \ "type").read[String] and
    (__).readOpt[GithubForkeEvent] and
    (__ \ "payload" \ "commits").readOpt[List[GithubPushEvent]] and
    (__ \ "created_at").read[DateTime](Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")) and
    (__ \ "actor" \ "url").read[String] and
    (__ \ "payload" \ "download").readOpt[GithubDownloadEvent])(GithubWallMessage.apply _)
}