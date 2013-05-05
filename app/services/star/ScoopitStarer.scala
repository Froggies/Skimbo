package services.star

import services.auth.providers.Scoopit

object ScoopitStarer extends GenericStarer {

  override val authProvider = Scoopit

  override def urlToStar(idProvider:String) = "http://www.scoop.it/api/1/post?action=thank&id="+idProvider

}