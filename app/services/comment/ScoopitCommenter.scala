package services.comment

import services.auth.providers.Scoopit
import java.net.URLEncoder

object ScoopitCommenter extends GenericCommenter {

  override val authProvider = Scoopit

  override def urlToComment(comment: models.Comment) = 
    "http://www.scoop.it/api/1/post?action=comment&id=" + comment.providerId +
      "&commentText=" + URLEncoder.encode(comment.message, "UTF-8")

}