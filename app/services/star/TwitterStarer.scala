package services.star

import services.auth.providers.Twitter

object TwitterStarer extends GenericStarer {

  override val authProvider = Twitter

  override def urlToStar(idProvider:String) = "https://api.twitter.com/1.1/statuses/retweet/"+idProvider+".json"
  
}