package services.endpoints

import parser.GenericParser
import parser.GenericParserParamHelper
import parser.json.detail.FacebookPostDetails
import parser.json.detail.GoogleplusDetails
import parser.json.detail.LinkedInPostDetails
import parser.json.detail.ScoopitPostDetails
import parser.json.detail.TweetDetails
import parser.json.providers.BetaseriesPlanningParser
import parser.json.providers.BetaseriesTimelineParser
import parser.json.providers.BitbucketCommitParser
import parser.json.providers.BitbucketEventsRepoParser
import parser.json.providers.BitbucketRepoParamHelper
import parser.json.providers.FacebookInboxParser
import parser.json.providers.FacebookUser
import parser.json.providers.FacebookWallParser
import parser.json.providers.GithubUser
import parser.json.providers.GithubWallParser
import parser.json.providers.GoogleplusUser
import parser.json.providers.GoogleplusWallParser
import parser.json.providers.LinkedInWallParser
import parser.json.providers.RedditMessageParser
import parser.json.providers.ScoopitTopic
import parser.json.providers.ScoopitTopicParamHelper
import parser.json.providers.ScoopitWallParser
import parser.json.providers.TrelloWallParser
import parser.json.providers.TwitterDirectMessageParser
import parser.json.providers.TwitterHashtagParser
import parser.json.providers.TwitterTimelineParser
import parser.json.providers.TwitterUser
import parser.json.providers.ViadeoInboxParser
import parser.json.providers.ViadeoWallParser
import parser.xml.GenericRssParser
import services.auth.GenericProvider
import services.auth.RssProvider
import services.auth.providers
import services.comment.Commenter
import services.comment.FacebookCommenter
import services.comment.LinkedInCommenter
import services.comment.ScoopitCommenter
import services.comment.TwitterCommenter
import services.star.FacebookStarer
import services.star.LinkedInStarer
import services.star.RedditStarer
import services.star.ScoopitStarer
import services.star.Starer
import services.star.TwitterStarer

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
      override val starer = Some(TwitterStarer)
      override val canParseResultStar = true
      override val commenter = Some(TwitterCommenter)
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://api.twitter.com/1.1/statuses/user_timeline.json?count=:limit&user_id=:username")
      override val since = wall.since
      override val delay = 80
      override val provider = providers.Twitter
      override val requiredParams = List("username")
      override val parser = Some(TwitterTimelineParser)
      override val mustBeReordered = true
      override val uniqueName = "twitter.user"
      override val paramParserHelper = Some(TwitterUser)
      override val paramUrlHelper = Some("https://api.twitter.com/1.1/users/search.json?q=:search&count=10")
      override val starer = Some(TwitterStarer)
      override val commenter = Some(TwitterCommenter)
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
      override val starer = Some(TwitterStarer)
      override val commenter = Some(TwitterCommenter)
      override val transformParams = Map("hashtag" -> ((s: String) => if (s.startsWith("#")) s.drop(1) else s))
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
      override val starer = Some(TwitterStarer)
      override val commenter = Some(TwitterCommenter)
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
      override val url = withLimit("https://graph.facebook.com/me/feed?limit=:limit&fields=from,type,status_type,comments,message,story,picture,likes,actions")
      override val since = Some("&since=:since")
      override val delay = 30
      override val provider = providers.Facebook
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.wall"
      override val parserDetails = Some(FacebookPostDetails)
      override val urlDetails = "https://graph.facebook.com/:id"
      override val starer = Some(FacebookStarer)
      override val commenter = Some(FacebookCommenter)
    }
    object notification extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/me/home?limit=:limit&fields=from,type,status_type,comments,message,story,picture,likes,actions")
      override val since = Some("&since=:since")
      override val delay = 30
      override val provider = providers.Facebook
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.notification"
      override val parserDetails = Some(FacebookPostDetails)
      override val urlDetails = "https://graph.facebook.com/:id"
      override val starer = Some(FacebookStarer)
      override val commenter = Some(FacebookCommenter)
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
      override val paramParserHelper = Some(FacebookUser)
      override val paramUrlHelper = Some("https://graph.facebook.com/search?q=:search&limit=10&type=user&fields=name,picture")
      override val starer = Some(FacebookStarer)
      override val commenter = Some(FacebookCommenter)
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
      override val paramParserHelper = Some(FacebookUser)
      override val paramUrlHelper = Some("https://graph.facebook.com/search?q=:search&limit=10&type=group&fields=name,picture")
      override val starer = Some(FacebookStarer)
      override val commenter = Some(FacebookCommenter)
    }
    object page extends EndpointConfig {
      override val url = withLimit("https://graph.facebook.com/:pageId/feed?limit=:limit")
      override val since = wall.since
      override val delay = 30
      override val provider = providers.Facebook
      override val requiredParams = List("pageId")
      override val parser = Some(FacebookWallParser)
      override val mustBeReordered = true
      override val uniqueName = "facebook.page"
      override val paramParserHelper = Some(FacebookUser)
      override val paramUrlHelper = Some("https://graph.facebook.com/search?q=:search&limit=10&type=page&fields=name,picture")
      override val starer = Some(FacebookStarer)
      override val commenter = Some(FacebookCommenter)
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
      override val url = withLimit("https://api.viadeo.com/me/smart_news.json?user_detail=PARTIAL&limit=:limit")
      override val since = Some("&since=:since")
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
      override val uniqueName = "viadeo.smartNews"
    }
    object newsfeed extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/newsfeed.json?user_detail=PARTIAL&limit=:limit")
      override val since = smartNews.since
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
      override val uniqueName = "viadeo.newsfeed"
    }
    object homeNewsfeed extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/me/home_newsfeed.json?user_detail=PARTIAL&limit=:limit")
      override val since = smartNews.since
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoWallParser)
      override val uniqueName = "viadeo.homeNewsfeed"
    }
    object inbox extends EndpointConfig {
      override val limit = 30
      override val url = withLimit("https://api.viadeo.com/me/inbox.json?user_detail=PARTIAL&limit=:limit")
      override val since = smartNews.since
      override val provider = providers.Viadeo
      override val parser = Some(ViadeoInboxParser)
      override val uniqueName = "viadeo.inbox"
    }
  }

  object Linkedin {
    object wall extends EndpointConfig {
      override val url = withLimit("http://api.linkedin.com/v1/people/~/network/updates?count=:limit")
      override val since = Some("&after=:since")
      override val provider = providers.LinkedIn
      override val parser = Some(LinkedInWallParser)
      override val uniqueName = "linkedin.wall"
      override val parserDetails = Some(LinkedInPostDetails)
      override val urlDetails = "http://www.scoop.it/api/1/post?id=:id"
      override val commenter = Some(LinkedInCommenter)
      override val starer = Some(LinkedInStarer)
    }
  }

  object GooglePlus {
    object wall extends EndpointConfig {
      override val url = withLimit("https://www.googleapis.com/plus/v1/people/me/activities/public?maxResults=:limit")
      override val mustBeReordered = true
      override val provider = providers.GooglePlus
      override val parser = Some(GoogleplusWallParser)
      override val uniqueName = "googleplus.wall"
      override val delay = 600
      override val parserDetails = Some(GoogleplusDetails)
      override val urlDetails = "https://www.googleapis.com/plus/v1/activities/:id"
    }
    object user extends EndpointConfig {
      override val url = withLimit("https://www.googleapis.com/plus/v1/people/:username/activities/public?maxResults=:limit")
      override val mustBeReordered = true
      override val provider = providers.GooglePlus
      override val requiredParams = List("username")
      override val parser = Some(GoogleplusWallParser)
      override val uniqueName = "googleplus.user"
      override val paramParserHelper = Some(GoogleplusUser)
      override val paramUrlHelper = Some("https://www.googleapis.com/plus/v1/people?query=:search")
      override val delay = 600
    }
  }

  object Github {
    object userEvents extends EndpointConfig {
      override val url = "https://api.github.com/users/:username/received_events"
      // override val since = need perso header ETag
      override val mustBeReordered = true
      override val provider = providers.GitHub
      override val requiredParams = List("username")
      override val parser = Some(GithubWallParser)
      override val uniqueName = "github.userEvents"
      override val paramParserHelper = Some(GithubUser)
      override val paramUrlHelper = Some("https://api.github.com/legacy/user/search/:search")
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
      override val starer = Some(ScoopitStarer)
      override val commenter = Some(ScoopitCommenter)
    }
    object topic extends EndpointConfig {
      override val url = withLimit("http://www.scoop.it/api/1/topic?count=:limit&id=:id")
      override val since = Some("&since=:since") // ts
      override val requiredParams = List("id")
      override val paramParserHelper = Some(ScoopitTopicParamHelper)
      override val paramUrlHelper = Some("http://www.scoop.it/api/1/search?type=topic&query=:search&count=10")
      override val provider = providers.Scoopit
      override val parser = Some(ScoopitTopic)
      override val uniqueName = "scoopit.topic"
      override val parserDetails = Some(ScoopitPostDetails)
      override val urlDetails = "http://www.scoop.it/api/1/post?id=:id"
      override val starer = Some(ScoopitStarer)
      override val commenter = Some(ScoopitCommenter)
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
  object Bitbucket {
    object eventsRepo extends EndpointConfig {
      override val url = withLimit("https://api.bitbucket.org/1.0/repositories/:id/events?limit=:limit")
      override val requiredParams = List("id")
      override val provider = providers.Bitbucket
      override val mustBeReordered = true
      override val parser = Some(BitbucketEventsRepoParser)
      override val uniqueName = "bitbucket.eventsRepo"
      override val paramParserHelper = Some(BitbucketRepoParamHelper)
      override val paramUrlHelper = Some("https://bitbucket.org/xhr/repositories?term=:search")
    }
    object commits extends EndpointConfig {
      override val url = withLimit("https://api.bitbucket.org/1.0/repositories/:id/changesets?limit=:limit")
      override val requiredParams = List("id")
      override val provider = providers.Bitbucket
      override val mustBeReordered = true
      override val parser = Some(BitbucketCommitParser)
      override val uniqueName = "bitbucket.commits"
      override val paramParserHelper = Some(BitbucketRepoParamHelper)
      override val paramUrlHelper = Some("https://bitbucket.org/xhr/repositories?term=:search")
    }
  }
  object Reddit {
    object hot extends EndpointConfig {
      override val url = withLimit("https://oauth.reddit.com/hot.json?limit=:limit")
      override val since = Some("&after=:since")
      override val provider = providers.Reddit
      override val parser = Some(RedditMessageParser)
      override val uniqueName = "reddit.hot"
      override val starer = Some(RedditStarer)
    }
    object top extends EndpointConfig {
      override val url = withLimit("https://oauth.reddit.com/top.json?limit=:limit")
      override val since = Reddit.hot.since
      override val provider = Reddit.hot.provider
      override val parser = Reddit.hot.parser
      override val uniqueName = "reddit.top"
      override val starer = Reddit.hot.starer
    }
    object newer extends EndpointConfig {
      override val url = withLimit("https://oauth.reddit.com/new.json?limit=:limit")
      override val since = Reddit.hot.since
      override val provider = Reddit.hot.provider
      override val parser = Reddit.hot.parser
      override val uniqueName = "reddit.new"
      override val starer = Reddit.hot.starer
    }
    object subreddit extends EndpointConfig {
      override val url = withLimit("https://oauth.reddit.com/r/:subreddit.json?limit=:limit")
      override val requiredParams = List("subreddit")
      override val since = Reddit.hot.since
      override val provider = Reddit.hot.provider
      override val parser = Reddit.hot.parser
      override val uniqueName = "reddit.subreddit"
      override val starer = Reddit.hot.starer
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
  val paramParserHelper: Option[GenericParserParamHelper] = None
  val paramUrlHelper: Option[String] = None
  val commenter: Option[Commenter] = None
  val transformParams: Map[String, (String) => String] = Map()

  def withLimit(url: String) = url.replace(":limit", limit.toString)
}
