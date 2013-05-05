package services.comment

import services.auth.providers.Facebook

object FacebookCommenter extends GenericCommenter {

  override val authProvider = Facebook

  override def urlToComment(comment: models.Comment) = {
    println("FACEBOOK ==> "+comment.providerId)
    "https://graph.facebook.com/" + comment.providerId + "/comments"
  }
    
  override def commentParams(idUser: String, comment: models.Comment): Seq[(String, String)] = {
    Seq("message" -> comment.message)
  }

}