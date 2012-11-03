package model

import org.joda.time._
import SocialNetwork._

/**
* Common format between socials network
*/
case class Skimbo(
	authorName: String, 
	authorScreenName: String,
	message: String,
	createdAt: DateTime,
	comments: List[Skimbo],
	from: SocialNetwork,
	shared: Int
)