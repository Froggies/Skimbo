package services.endpoints

import services.auth.GenericProvider
import services.auth.providers
import json.parser._
import org.joda.time.format.DateTimeFormat

object Configuration {

  object Twitter {

    object wall extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/home_timeline.json?count=:limit")
      override val since = "&since_id=:since"//ID
      override val delay = 60
      override val provider = providers.Twitter
      override val parser = Some(TwitterTimelineParser)
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/user_timeline.json?count=:limit&screen_name=:username")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Twitter
      override val requiredParams = List("username")
      override val parser = Some(TwitterTimelineParser)
    }
    object hashtag extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/search/tweets.json?count=:limit&result_type=mixed&q=%23:hashtag")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Twitter
      override val requiredParams = List("hashtag")
      override val parser = Some(TwitterHashtagParser)
    }
  }

  object Facebook {
    object wall extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/me/home?limit=:limit")
      override val since = "&since=:since"
      override val delay = 30
      override val provider = providers.Facebook
      override val parser = Some(FacebookWallParser)
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/:username/feed?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val requiredParams = List("username")
    }
    object group extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/:groupId/feed?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val requiredParams = List("groupId")
    }
  }

  object Viadeo {
    object wall extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/me/smart_news.json?limit=:limit")
      override val since = "&since=:since"//date = ISO-8601 [YYYY-MM-DD'T'HH:MM:SSZZ]
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
    }
  }

  object Linkedin {
    object wall extends EndpointConfig {
      override val url = withLimit("http://api.linkedin.com/v1/people/~/network/updates?count=:limit")
      override val since = "&after=:since"//timestamp = milliseconds since the epoch
      override val provider = providers.LinkedIn
      override val parser = Some(LinkedInWallParser)
    }
  }

  object GooglePlus {
    object wall extends EndpointConfig {
      override val url = withLimit("https://www.googleapis.com/plus/v1/people/me/activities/public?maxResults=:limit")
      override val manualNextResults = true // Fuck Google+, there is no "since" key... Must be handle in actor with cache...
      override val provider = providers.GooglePlus
      override val parser = Some(GoogleplusWallParser)
    }
  }

  object Github {
    object notifications extends EndpointConfig {
      override val url = "https://api.github.com/users/:username/received_events" // (default limit : 30)
      override val manualNextResults = true // Fuck too (http://developer.github.com/v3/#conditional-requests)
      override val provider = providers.GitHub
      override val requiredParams = List("username")
      override val parser = Some(GithubWallParser)
    }
  }

  object Trello {
    object notifications extends EndpointConfig {
      override val url = withLimit("https://api.trello.com/1/members/me/notifications?limit=:limit")
      override val since = "&since=:since" // id
      override val provider = providers.Trello
      override val parser = Some(TrelloWallParser)
    }
  }

  object StackOverflow {

  }

  object Scoopit {
    object notifications extends EndpointConfig {
      override val url = "http://www.scoop.it/api/1/notifications"
      override val since = "?since=:since" // ts
      override val provider = providers.Scoopit
    }
  }
}


trait EndpointConfig {
  val url: String
  val since: String = ""
  val delay: Int = 60
  val provider: GenericProvider
  val requiredParams : List[String] = List.empty
  val limit = 20
  val manualNextResults = false
  val parser: Option[GenericParser] = None

  def withLimit(url: String) = url.replace(":limit", limit.toString)
}