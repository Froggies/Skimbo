package services.comment

import services.auth.providers.Viadeo

object ViadeoCommenter extends GenericCommenter {

  override val authProvider = Viadeo

  override def urlToComment(comment: models.Comment) = "none found for now but mail was send"

}