package services.comment

trait GenericCommenter extends Commenter {

  def comment(idUser: String, comment:models.Comment) = {
    val url = urlToComment(comment)
    val params = commentParams(idUser, comment)
    val headers = commentHeaderParams
    val content = commentContent(comment)
    authProvider.post(idUser, url, params, headers, content)
  }
  
}