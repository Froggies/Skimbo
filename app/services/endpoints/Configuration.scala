package services.endpoints

import services.auth.GenericProvider
import services.auth.providers

object Configuration {

  object Twitter { 
    
    object wall extends EndpointConfig {
      override val url = "https://api.twitter.com/1.1/statuses/home_timeline.json?count=50"
      override val since = "&since_id=:since"
      override val delay = 60
      override val provider = providers.Twitter
    }

    object user extends EndpointConfig {
      override val url = "https://api.twitter.com/1.1/statuses/user_timeline.json?count=50&screen_name=:username" 
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Twitter
      override val requiredParams = List("username")
    }

    object hashtag extends EndpointConfig {
      override val url = "https://api.twitter.com/1.1/search/tweets.json?count=50&result_type=mixed&q=%23:hashtag"
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Twitter
      override val requiredParams = List("hashtag")
    }
  }

  object Facebook {
    object wall extends EndpointConfig {
      override val url = "https://graph.facebook.com/me/home?limit=50"
      override val since = "&since=:since"
      override val delay = 30
      override val provider = providers.Facebook
    }
  }

}

trait EndpointConfig {
  val url: String
  val since: String
  val delay: Int
  val provider: GenericProvider
  val requiredParams : List[String] = List.empty
}