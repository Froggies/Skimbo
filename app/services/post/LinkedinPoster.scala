package services.post

import services.auth.providers.LinkedIn

object LinkedinPoster extends GenericPoster {

  override val authProvider = LinkedIn

  override def urlToPost(post:models.Post) = "http://api.linkedin.com/v1/people/~/shares"
  
  override def postContent(post:models.Post):String = {
    """<?xml version="1.0" encoding="UTF-8"?>
    <share>
      <comment>""" +post.title+ """</comment>
      <content>  
        <description>""" +post.message+ """</description> 
        <submitted-url>""" +post.url.getOrElse("")+ """</submitted-url> 
        <submitted-image-url>""" +post.url_image.getOrElse("")+ """</submitted-image-url> 
      </content>
      <visibility>
        <code>anyone</code>
      </visibility>
    </share>"""
  }
    
}