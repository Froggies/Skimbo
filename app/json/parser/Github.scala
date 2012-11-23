package json.parser

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import json.Skimbo
import services.auth.providers.GitHub
import org.joda.time.format.DateTimeFormat

case class GithubWallMessage(
  id: String,
  actorLogin: String,
  typeGithub: String,
  fork: Option[GithubForkeEvent],
  head: Option[String],
  push: Option[Seq[GithubPushEvent]],
  createdAt: DateTime,
  avatarUser: Option[String],
  repoName: String,
  download: Option[GithubDownloadEvent])

case class GithubForkeEvent(
  url: Option[String],
  repoName: String)

case class GithubPushEvent(
  message: String,
  url: String,
  name: String)

case class GithubDownloadEvent(
  name: String,
  description: String,
  url: String)

object GithubWallParser extends GenericParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    val e = Json.fromJson[GithubWallMessage](json).get
    Some(Skimbo(
      e.actorLogin,
      e.actorLogin,
      buildMsg(e),
      e.createdAt,
      Nil,
      -1,
      buildLink(e),
      e.createdAt.toString(GithubWallMessage.datePattern),
      e.avatarUser,
      GitHub))
  }

  def buildMsg(e: GithubWallMessage) = {
    e.typeGithub match {
      case "ForkEvent" => "Fork of " + e.fork.get.repoName
      case "PushEvent" => {
        "Push on " + e.repoName + " : " + e.push.get.head.message
      }
      case "DownloadEvent" => {
        if (!e.download.get.description.isEmpty()) {
          "New download (" + e.download.get.description + ") : " + e.download.get.name
        } else {
          "New download : " + e.download.get.name
        }

      }
      case _ => "Undevelopped type on " + e.repoName + " : " + e.typeGithub
    }
  }

  val gitPushUrl = "https://github.com/%s/commit/%s"

  def buildLink(e: GithubWallMessage) = {
    e.typeGithub match {
      case "ForkEvent" => e.fork.get.url
      case "PushEvent" => {
        val push = e.push.get.head
        Some(gitPushUrl.format(e.repoName, e.head.get))
      }
      case "DownloadEvent" => Some(e.download.get.url)
      case _ => None
    }
  }
  
}

object GithubForkeEvent {
  implicit val githubReader: Reads[GithubForkeEvent] = (
    (__ \ "payload" \ "forkee" \ "html_url").readOpt[String] and
    (__ \ "repo" \ "name").read[String])(GithubForkeEvent.apply _)
}

object GithubPushEvent {
  implicit val githubReader: Reads[GithubPushEvent] = (
    (__ \ "message").read[String] and
    (__ \ "url").read[String] and
    (__ \ "author" \ "name").read[String])(GithubPushEvent.apply _)
}

object GithubDownloadEvent {
  implicit val githubReader: Reads[GithubDownloadEvent] = (
    (__ \ "name").read[String] and
    (__ \ "description").read[String] and
    (__ \ "html_url").read[String])(GithubDownloadEvent.apply _)
}

object GithubWallMessage {
  
  val datePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  
  implicit val githubReader: Reads[GithubWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "actor" \ "login").read[String] and
    (__ \ "type").read[String] and
    (__).readOpt[GithubForkeEvent] and
    (__ \ "payload" \ "head").readOpt[String] and
    (__ \ "payload" \ "commits").readOpt[List[GithubPushEvent]] and
    (__ \ "created_at").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "actor" \ "avatar_url").readOpt[String] and
    (__ \ "repo" \ "name").read[String] and
    (__ \ "payload" \ "download").readOpt[GithubDownloadEvent])(GithubWallMessage.apply _)
}