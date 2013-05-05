package services.star

import services.auth.providers.LinkedIn

object LinkedInStarer extends GenericStarer {

  override val authProvider = LinkedIn

  override def urlToStar(idProvider:String) = 
    "http://api.linkedin.com/v1/people/~/network/updates/key=" + idProvider + "/is-liked"

  override def starContent(idProvider:String):String = {
    """<?xml version='1.0' encoding='UTF-8'?>
        <is-liked>true</is-liked>  """
  }
    
}