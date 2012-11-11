package model

import org.joda.time._
import SocialNetwork._

/**
* Common format between social networks
*/
case class Skimbo(
  authorName: String,
  authorScreenName: String,
  message: String,
  createdAt: DateTime,
  comments: List[Skimbo],
  shared: Int,
  directLink: Option[String],
  sinceId: String,
  from: SocialNetwork
)