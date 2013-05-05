package services.star

import services.auth.providers.Facebook

object FacebookStarer extends GenericStarer {

  override val authProvider = Facebook

  override def urlToStar(idProvider:String) = "https://graph.facebook.com/"+idProvider+"/likes"

}