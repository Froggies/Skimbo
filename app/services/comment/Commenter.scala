package services.comment

import models.Comment
import services.auth.AuthProvider

trait Commenter {

  val authProvider: AuthProvider

  lazy val name: String = authProvider.name

  def urlToComment(comment: Comment): String

  def commentHeaderParams(): Seq[(String, String)] = Seq.empty

  def commentParams(idUser: String, comment: Comment): Seq[(String, String)] = Seq.empty

  def commentContent(comment: Comment): String = ""

  def comment(idUser: String, comment: Comment)

}

object Commenters {

  val commenters: Seq[Commenter] = Seq(
    TwitterCommenter,
    FacebookCommenter)
  //    FacebookPagePoster,
  //    LinkedinPoster,
  //    GithubPoster,
  //    GoogleplusPoster,
  //    ScoopitPoster,
  //    ViadeoPoster)

  def getCommenter(name: String): Option[Commenter] = {
    commenters.find(_.name == name)
  }

}