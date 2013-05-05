package services.comment

import services.auth.providers.LinkedIn

object LinkedInCommenter extends GenericCommenter {

  override val authProvider = LinkedIn

  override def urlToComment(comment: models.Comment) = 
    "http://api.linkedin.com/v1/people/~/network/updates/key="+comment.providerId+"/update-comments"
  
  override def commentContent(comment:models.Comment):String = {
    """<?xml version='1.0' encoding='UTF-8'?>
        <update-comment>
          <comment>""" + comment.message + """</comment>
        </update-comment>"""
  }

}