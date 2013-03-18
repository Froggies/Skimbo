package services.endpoints

import services.auth.GenericProvider
import services.auth.providers
import org.joda.time.format.DateTimeFormat
import parser.GenericParser
import parser.json.providers._
import parser.json.detail._
import services.auth.RssProvider
import parser.xml.GenericRssParser
import parser.GenericParser
import services.post.Starer

object Configuration {

  object Twitter {
    object wall extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/home_timeline.json?count=:limit")
      override val since = Some("&since_id=:since")//ID
      override val delay = 80
      override val provider = providers.Twitter
      override val parser = Some(TwitterTimelineParser)
      override val uniqueName = "twitter.wall"
      override val parserDetails = Some(TweetDetails)
      override val urlDetails = "https://api.twitter.com/1.1/statuses/show.json?id=:id"
      override val starer = Some(providers.Twitter)
      override val canParseResultStar = true
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/user_timeline.json?count=:limit&screen_name=:username")
      override val since = wall.since
      override val delay = 80
      override val provider = providers.Twitter
      override val requiredParams = List("username")
      override val parser = Some(TwitterTimelineParser)
      override val mustBeReordered = true
      override val uniqueName = "twitter.user"
    }
    object hashtag extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/search/tweets.json?count=:limit&result_type=mixed&q=%23:hashtag")
      override val since = wall.since
      override val delay = 80
      override val provider = providers.Twitter
      override val requiredParams = List("hashtag")
      override val parser = Some(TwitterHashtagParser)
      override val mustBeReordered = true
      override val uniqueName = "twitter.hashtag"
    }
    object directMessage extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/direct_messages.json?count=:limit")
      override val since = wall.since
      override val delay = 80
      override val provider = providers.Twitter
      override val parser = Some(TwitterDirectMessageParser)
      override val uniqueName = "twitter.directMessage"
    }
    object messageToMe extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/mentions_timeline.json?count=:limit")
      override val since = wall.since
      override val delay = 80
      override val provider = providers.Twitter
      override val parser = Some(TwitterTimelineParser)
      override val uniqueName = "twitter.messageToMe"
    }
//    object connect extends EndpointConfig {
//      override val url = "https://api.twitter.com/1.1/friends/ids.json"
//      override val delay = 80
//      override val provider = providers.Twitter
//      //override val parser = Some(TwitterConnectParser)//more complexe than expected
//    }
  }

  object Facebook {
    object wall extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/me/home?limit=:limit")
      override val since = Some("&since=:since")
      override val delay = 30
      override val provider = providers.Facebook
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.wall"
      override val parserDetails = Some(FacebookPostDetails)
      override val urlDetails = "https://graph.facebook.com/:id"
      override val starer = Some(providers.Facebook)
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/:username/feed?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val requiredParams = List("username")
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.user"
    }
    object group extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/:groupId/feed?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val requiredParams = List("groupId")
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.group"
    }
    object message extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/me/inbox?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val parser = Some(FacebookInboxParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.message"
    }
  }

  object Viadeo {
    object smartNews extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/me/smart_news.json?limit=:limit")
      override val since = Some("&since=:since")
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
      override val uniqueName = "viadeo.smartNews"
    }
    object newsfeed extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/newsfeed.json?limit=:limit")
      override val since = smartNews.since
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
      override val uniqueName = "viadeo.newsfeed"
    }
  }

  object Linkedin {
    object wall extends EndpointConfig {
      override val url = withLimit("http://api.linkedin.com/v1/people/~/network/updates?count=:limit")
      override val since = Some("&after=:since")
      override val provider = providers.LinkedIn
      override val parser = Some(LinkedInWallParser)
      override val uniqueName = "linkedin.wall"
    }
  }

  object GooglePlus {
    object wall extends EndpointConfig {
      override val url = withLimit("https://www.googleapis.com/plus/v1/people/me/activities/public?maxResults=:limit")
      override val provider = providers.GooglePlus
      override val parser = Some(GoogleplusWallParser)
      override val uniqueName = "googleplus.wall"
    }
//    object user extends EndpointConfig {
//      override val url = withLimit("https://www.googleapis.com/plus/v1/people/:username/activities/public?maxResults=:limit")
//      override val manualNextResults = true
//      override val provider = providers.GooglePlus
//      override val requiredParams = List("username")
//      override val parser = GoogleplusWallParser
//      override val uniqueName = ""
//    }
  }

  object Github {
    object notifications extends EndpointConfig {
      override val url = "https://api.github.com/users/:username/received_events"
      // override val since = need perso header ETag
      override val mustBeReordered = true
      override val provider = providers.GitHub
      override val requiredParams = List("username")
      override val parser = Some(GithubWallParser)
      override val uniqueName = "github.notifications"
    }
  }

  object Trello {
    object notifications extends EndpointConfig {
      override val url = withLimit("https://api.trello.com/1/members/me/notifications?limit=:limit")
      //DON'T REMOVE THIS COMMENT BECAUSE TRELLO SINCEID BUG AND RETURN SAME POST MULTIPLE TIME
      //override val since = Some("&since=:since") //id 
      override val mustBeReordered = true
      override val provider = providers.Trello
      override val parser = Some(TrelloWallParser)
      override val uniqueName = "trello.notifications"
    }
  }

  object StackOverflow {

  }

  object Scoopit {
    object wall extends EndpointConfig {
      override val url = withLimit("http://www.scoop.it/api/1/compilation?count=:limit")
      override val since = Some("&since=:since") // ts
      override val provider = providers.Scoopit
      override val parser = Some(ScoopitWallParser)
      override val uniqueName = "scoopit.wall"
      override val parserDetails = Some(ScoopitPostDetails)
      override val urlDetails = "http://www.scoop.it/api/1/post?id=:id"
      override val starer = Some(providers.Scoopit)
    }
  }
  
  object BetaSeries {
    object planning extends EndpointConfig {
      override val url = "http://api.betaseries.com/planning/member.json"
      override val provider = providers.BetaSeries
      override val mustBeReordered = true
      override val parser = Some(BetaseriesPlanningParser)
      override val delay = 600
      override val uniqueName = "betaseries.planning"
    }
    object timeline extends EndpointConfig {
      override val url = withLimit("http://api.betaseries.com/timeline/friends.json?number=:limit")
      override val provider = providers.BetaSeries
      override val mustBeReordered = true
      override val parser = Some(BetaseriesTimelineParser)
      override val delay = 600
      override val uniqueName = "betaseries.timeline"
    }
  }
  object Rss {
    object rss extends EndpointConfig {
      override val url = ":url"
      override val requiredParams = List("url")
      override val provider = RssProvider
      override val mustBeReordered = true
      override val parser = Some(GenericRssParser)
      override val delay = 500
      override val uniqueName = "rss.rss"
    }
  }
}


trait EndpointConfig {
  val url: String
  val since: Option[String] = None
  val delay: Int = 60
  val provider: GenericProvider
  val requiredParams : List[String] = List.empty
  val limit = 20
  val parser: Option[GenericParser] = None
  val mustBeReordered = false
  val parserDetails : Option[GenericParser] = None
  val uniqueName: String
  val urlDetails:String = ""
  val starer: Option[Starer] = None
  val canParseResultStar: Boolean = false

  def withLimit(url: String) = url.replace(":limit", limit.toString)
}