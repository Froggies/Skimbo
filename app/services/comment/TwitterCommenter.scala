package services.comment

import services.auth.providers.Twitter
import java.net.URLEncoder

object TwitterCommenter extends GenericCommenter {

  override val authProvider = Twitter

  override def urlToComment(comment: models.Comment) =
    "https://api.twitter.com/1.1/statuses/update.json?status=" + 
      URLEncoder.encode(comment.message, "UTF-8") + 
      "&in_reply_to_status_id=" + 
      comment.providerId
  
}