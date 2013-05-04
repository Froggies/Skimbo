package services.post

import java.net.URLEncoder
import play.api.libs.ws.Response
import play.api.mvc.RequestHeader
import services.auth.providers.Twitter
import services.comment.Commenter
import models.Comment

object TwitterPoster extends GenericPoster {

  override val authProvider = Twitter

  override def urlToPost(post: models.Post) =
    "https://api.twitter.com/1.1/statuses/update.json?status=" + URLEncoder.encode(post.message, "UTF-8")

}