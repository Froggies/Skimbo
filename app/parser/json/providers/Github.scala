package parser.json.providers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
import parser.json.SkimboJsPath._
import models.Skimbo
import parser.json.GenericJsonParser
import services.auth.providers.GitHub

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
  issues: Option[GithubIssuesEvent],
  issueComment: Option[GithubCommentEvent],
  download: Option[GithubDownloadEvent],
  gollum: Option[GithubGollumEvent],
  pullRequest: Option[GithubPullRequestEvent],
  pullRequestComment: Option[GithubPullRequestReviewCommentEvent],
  refType:Option[String],
  refName:Option[String])

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
  
case class GithubIssuesEvent(
  title: String,
  state: String,
  htmlUrl: String,
  body: String)
  
case class GithubCommentEvent(
  id: Int,
  body: String,
  htmlUrl: Option[String])
  
case class GithubGollumEvent(
  action: String,
  pageName: String,
  pageUrl: String
)

case class GithubPullRequestReviewCommentEvent(
  id: Int,
  body: String,
  htmlUrl: Option[String]
)

case class GithubPullRequestEvent(
  body:String,
  url:String
)

object GithubWallParser extends GenericJsonParser {

  override def asSkimbo(json: JsValue): Option[Skimbo] = {
    Json.fromJson[GithubWallMessage](json).fold(
      error => logParseError(json, error, "GithubWallMessage"),
      e => Some(Skimbo(
        e.id,
        e.actorLogin,
        e.actorLogin,
        buildMsg(e),
        e.createdAt,
        Nil,
        -1,
        buildLink(e),
        e.createdAt.toString(GithubWallMessage.datePattern),
        e.avatarUser,
        GitHub)))
  }

  def buildMsg(e: GithubWallMessage) = {
    e.typeGithub match {
      case "ForkEvent" => "Fork of " + e.fork.map(_.repoName).getOrElse("")
      case "PushEvent" => "Push on " + e.repoName + ": " + e.push.get.head.message
      case "IssuesEvent" => "Issue [" + e.issues.get.title + "] > " + e.issues.get.state + ": " + e.issues.get.body
      case "IssueCommentEvent" => "Issue [" + e.issues.get.title + "] > " + e.issueComment.get.body
      case "CommitCommentEvent" => "Comment on commit : " + e.issueComment.get.body
      case "DeleteEvent" => "Delete " + e.refType.getOrElse("") + " " + e.refName.getOrElse("")
      case "CreateEvent" => "Create " + e.refType.getOrElse("") + " " + e.refName.getOrElse("")
      case "DownloadEvent" =>
        if (!e.download.get.description.isEmpty()) {
          "New download (" + e.download.get.description + ") : " + e.download.get.name
        } else {
          "New download: " + e.download.get.name
        }
      case "WatchEvent" => "Watch " + e.repoName
      case "GollumEvent" => "Has " + e.gollum.get.action + " " + e.gollum.get.pageName + " on wiki"
      case "PullRequestEvent" => "PullRequest : " + e.pullRequest.get.body
      case "PullRequestReviewCommentEvent" => "Comment on pull : " + e.pullRequestComment.map(_.body).getOrElse("")
      case _ => "TODO type on " + e.repoName + " : " + e.typeGithub
    }
  }

  val gitPushUrl = "https://github.com/%s/commit/%s"
  val gitRepoUrl = "https://github.com/%s"

  def buildLink(e: GithubWallMessage) = {
    e.typeGithub match {
      case "ForkEvent"          => e.fork.flatMap(_.url)
      case "PushEvent"          => Some(gitPushUrl.format(e.repoName, e.head.get))
      case "DownloadEvent"      => Some(e.download.get.url)
      case "IssuesEvent"        => Some(e.issues.get.htmlUrl)
      case "IssueCommentEvent"  => Some(e.issues.get.htmlUrl + "#issuecomment-" + e.issueComment.get.id)
      case "CommitCommentEvent" => Some(e.issueComment.get.htmlUrl.get)
      case "DeleteEvent"        => Some(gitRepoUrl.format(e.repoName))
      case "CreateEvent"        => Some(gitRepoUrl.format(e.repoName))
      case "WatchEvent"         => Some(gitRepoUrl.format(e.repoName))
      case "GollumEvent"        => Some(e.gollum.get.pageUrl)
      case "PullRequestEvent"   => Some(e.pullRequest.get.url)
      case "PullRequestReviewCommentEvent" => e.pullRequestComment.flatMap(_.htmlUrl)
      case _ => None
    }
  }
  
}

object GithubForkeEvent {
  implicit val githubReader: Reads[GithubForkeEvent] = (
    (__ \ "payload" \ "forkee" \ "html_url").readNullable[String] and
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

object GithubIssuesEvent {
  implicit val githubReader: Reads[GithubIssuesEvent] = (
    (__ \ "title").read[String] and
    (__ \ "state").read[String] and
    (__ \ "html_url").read[String] and
    (__ \ "body").read[String])(GithubIssuesEvent.apply _)
}

object GithubCommentEvent {
  implicit val githubReader: Reads[GithubCommentEvent] = (
    (__ \ "id").read[Int] and
    (__ \ "body").read[String] and
    (__ \ "html_url").readNullable[String])(GithubCommentEvent.apply _)
}

object GithubPullRequestReviewCommentEvent {
  implicit val githubReader: Reads[GithubPullRequestReviewCommentEvent] = (
    (__ \ "id").read[Int] and
    (__ \ "body").read[String] and
    (__ \ "_links" \ "html" \ "href").tryReadNullable[String])(GithubPullRequestReviewCommentEvent.apply _)
}

object GithubGollumEvent {
  implicit val githubReader: Reads[GithubGollumEvent] = (
    (__ \ "action").read[String] and
    (__ \ "page_name").read[String] and
    (__ \ "html_url").read[String])(GithubGollumEvent.apply _)
}

object GithubPullRequestEvent {
  implicit val githubReader: Reads[GithubPullRequestEvent] = (
    (__ \ "body").read[String] and
    (__ \ "_links" \ "html" \ "href").read[String])(GithubPullRequestEvent.apply _)
}

object GithubWallMessage {
  
  val datePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  
  implicit val githubReader: Reads[GithubWallMessage] = (
    (__ \ "id").read[String] and
    (__ \ "actor" \ "login").read[String] and
    (__ \ "type").read[String] and
    (__).tryReadNullable[GithubForkeEvent] and
    (__ \ "payload" \ "head").readNullable[String] and
    (__ \ "payload" \ "commits").readNullable[List[GithubPushEvent]] and
    (__ \ "created_at").read[DateTime](Reads.jodaDateReads(datePattern)) and
    (__ \ "actor" \ "avatar_url").readNullable[String] and
    (__ \ "repo" \ "name").read[String] and
    (__ \ "payload" \ "issue").readNullable[GithubIssuesEvent] and
    (__ \ "payload" \ "comment").readNullable[GithubCommentEvent] and
    (__ \ "payload" \ "download").readNullable[GithubDownloadEvent] and
    ((__ \ "payload" \ "pages")(0)).tryReadNullable[GithubGollumEvent] and
    (__ \ "payload" \ "pull_request").readNullable[GithubPullRequestEvent] and
    (__ \ "payload" \ "comment").readNullable[GithubPullRequestReviewCommentEvent] and
    (__ \ "payload" \ "ref_type").readNullable[String] and
    (__ \ "payload" \ "ref").readNullable[String])(GithubWallMessage.apply _)
}